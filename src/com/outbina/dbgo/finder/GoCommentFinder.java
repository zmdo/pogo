package com.outbina.dbgo.finder;

import com.goide.GoCommentsConverter;
import com.goide.psi.GoFieldDeclaration;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;

import java.util.Arrays;

public class GoCommentFinder {

    public static PsiComment find(PsiElement psiElement) {
        // 如果传入的Element本身不属于注释
        if (psiElement instanceof PsiElement) {
            // 寻找同一行的注释
            PsiElement element = psiElement.getNextSibling();
            while (true) {
                if(element instanceof PsiWhiteSpace) {
                    if(((PsiWhiteSpace)element).getText().contains("\n")){
                        break;
                    }
                } else if (element == null || element instanceof GoFieldDeclaration) {
                    break;
                } else if(element instanceof PsiComment) {
                    return (PsiComment)element;
                }
                element = element.getNextSibling();
            }
        }
        return null;
    }

    public static String findCommentString(PsiElement psiElement) {
        PsiComment comment = find(psiElement);
        if (comment != null) {
            return GoCommentsConverter.toStringList(Arrays.asList(comment)).get(0);
        }
        return "";
    }

}
