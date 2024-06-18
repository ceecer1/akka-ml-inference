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

public class HuggingFaceBertInference {
    public static void main(String[] args) throws IOException, TranslateException, ModelException {
        String question = "How many apples does Adam have finally?";
        String paragraph =
                "Adam had 10 apples. He gave 5 apples to Eve and 3 apples to James. Eve gave 3 to Kylie and 1 to James." +
                        "James gave 2 to Adam.";
        QAInput input = new QAInput(question, paragraph);

        String answer = HuggingFaceBertInference.qa_predict(input);
        System.out.println("The answer is: \n" + answer);
    }

    public static String qa_predict(QAInput input) throws IOException, TranslateException, ModelException {
        BertTranslatorLarge translator = new BertTranslatorLarge();
        Criteria<QAInput, String> criteria = Criteria.builder()
                .setTypes(QAInput.class, String.class)
                .optModelPath(Paths.get("src/main/resources/bert-large/bert-large-uncased-whole-word-masking-finetuned-squad.pt"))
                .optTranslator(translator)
                .optProgress(new ProgressBar()).build();

        ZooModel<QAInput, String> model = criteria.loadModel();
        try (Predictor<QAInput, String> predictor = model.newPredictor(translator)) {
            return predictor.predict(input);
        }
    }
}
