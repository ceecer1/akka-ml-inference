import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.Vocabulary;
import ai.djl.modality.nlp.bert.BertToken;
import ai.djl.modality.nlp.bert.BertTokenizer;
import ai.djl.modality.nlp.qa.QAInput;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BertTranslatorLarge implements Translator<QAInput, String> {
    private List<String> tokens;
    private Vocabulary vocabulary;
    private BertTokenizer tokenizer;

    @Override
    public void prepare(TranslatorContext ctx) throws IOException {
        Path path = Paths.get("src/main/resources/bert-large/vocab.txt");
        vocabulary = DefaultVocabulary.builder()
                .optMinFrequency(1)
                .addFromTextFile(path)
                .optUnknownToken("[UNK]")
                .build();
        tokenizer = new BertTokenizer();
    }

    @Override
    public NDList processInput(TranslatorContext ctx, QAInput input) throws IOException {
        BertToken token =
                tokenizer.encode(
                        input.getQuestion().toLowerCase(),
                        input.getParagraph().toLowerCase());

        // get the encoded tokens that would be used in processOutput
        tokens = token.getTokens();
        NDManager manager = ctx.getNDManager();
        // map the tokens(String) to indices(long)
        long[] indices = tokens.stream().mapToLong(vocabulary::getIndex).toArray();
        long[] attentionMask = token.getAttentionMask().stream().mapToLong(i -> i).toArray();
        long[] tokenType = token.getTokenTypes().stream().mapToLong(i -> i).toArray();
        NDArray indicesArray = manager.create(indices);
        NDArray attentionMaskArray =
                manager.create(attentionMask);
//        NDArray tokenTypeArray = manager.create(tokenType);
        // The order matters
//        return new NDList(indicesArray, attentionMaskArray, tokenTypeArray);
        return new NDList(indicesArray, attentionMaskArray);
    }

    @Override
    public String processOutput(TranslatorContext ctx, NDList list) {
        NDArray startLogits = list.get(0);
        NDArray endLogits = list.get(1);
        int startIdx = (int) startLogits.argMax().getLong();
        int endIdx = (int) endLogits.argMax().getLong();
        return tokenizer.tokenToString(tokens.subList(startIdx, endIdx + 1));
    }

    @Override
    public Batchifier getBatchifier() {
        return Batchifier.STACK;
    }
}