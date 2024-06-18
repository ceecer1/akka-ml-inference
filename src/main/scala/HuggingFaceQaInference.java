import ai.djl.Application;
import ai.djl.Device;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.nlp.Vocabulary;
import ai.djl.modality.nlp.bert.BertFullTokenizer;
import ai.djl.modality.nlp.bert.BertToken;
import ai.djl.modality.nlp.qa.QAInput;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.nio.file.Paths;

public class HuggingFaceQaInference {
    public static void main(String[] args) throws IOException, TranslateException, ModelException {
        String question = "What was Lightbend formerly known as?";
        String paragraph =
                "Lightbend started its journey in 2010 as Typesafe "
                        + "As of today we are known as Lightbend. "
                        + "We released Kalix after 10 years.";
        QAInput input = new QAInput(question, paragraph);

        String answer = HuggingFaceQaInference.qa_predict(input);
        System.out.println("The answer is: \n" + answer);
    }

    public static String qa_predict(QAInput input) throws IOException, TranslateException, ModelException {
        BertTranslator translator = new BertTranslator();
        Criteria<QAInput, String> criteria = Criteria.builder()
                .setTypes(QAInput.class, String.class)
                .optModelPath(Paths.get("src/main/resources/bert-works/trace_cased_bertqa.pt"))
                .optTranslator(translator)
                .optProgress(new ProgressBar()).build();

        ZooModel<QAInput, String> model = criteria.loadModel();
        try (Predictor<QAInput, String> predictor = model.newPredictor(translator)) {
            return predictor.predict(input);
        }
    }
}
