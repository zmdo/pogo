package com.outbina.dbgo.i18n;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class PogoBundle extends DynamicBundle {

    public static final String PATH = "messages.PogoBundle";
    private static final PogoBundle INSTANCE = new PogoBundle();

    private PogoBundle() { super(PATH); }

    @NotNull
    public static String message(@NotNull @PropertyKey(resourceBundle = PATH) String key, @NotNull Object... params) {
        return INSTANCE.getMessage(key, params);
    }

}
