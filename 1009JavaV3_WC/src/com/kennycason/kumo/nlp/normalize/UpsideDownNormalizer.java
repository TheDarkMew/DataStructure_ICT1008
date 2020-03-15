package com.kennycason.kumo.nlp.normalize;

/**
 * Created by kenny on 7/1/14.
 */
public class UpsideDownNormalizer implements Normalizer {

    private static final String normal = "abcdefghijklmnopqrstuvwxyz_,;.?!/\\'" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
    private static final String split  = "�?qɔp�?ɟbɥıظʞןɯuodbɹsʇnʌ�?xʎz‾'؛˙¿¡/\\," + "∀qϽᗡƎℲƃHIſʞ˥WNOԀὉᴚS⊥∩ΛMXʎZ" + "0Ɩᄅ�?ㄣϛ9ㄥ86";

    @Override
    public String apply(final String text) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            final char letter = text.charAt(i);
            final int index = normal.indexOf(letter);
            stringBuilder.append((index != -1) ? split.charAt(index) : letter);
        }
        return stringBuilder.reverse().toString();
    }
}
