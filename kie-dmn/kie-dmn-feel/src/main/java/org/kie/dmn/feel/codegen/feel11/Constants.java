/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package org.kie.dmn.feel.codegen.feel11;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.body.FieldDeclaration;
import org.drools.javaparser.ast.body.VariableDeclarator;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.LambdaExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.ObjectCreationExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.Type;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;

public class Constants {

    public static final Expression DECIMAL_128 = JavaParser.parseExpression("java.math.MathContext.DECIMAL128");
    public static final ClassOrInterfaceType BigDecimalT = new ClassOrInterfaceType(BigDecimal.class.getCanonicalName());
    public static final ClassOrInterfaceType BooleanT = new ClassOrInterfaceType(Boolean.class.getCanonicalName());
    private static final org.drools.javaparser.ast.type.Type ListT =
            JavaParser.parseType(List.class.getCanonicalName());
    public static final ClassOrInterfaceType UnaryTestT = JavaParser.parseClassOrInterfaceType(UnaryTest.class.getCanonicalName());
    public static final String RangeBoundary =
            Range.RangeBoundary.class.getCanonicalName();
    public static final Expression BuiltInTypeT = JavaParser.parseExpression("org.kie.dmn.feel.lang.types.BuiltInType");
    public static final ClassOrInterfaceType FunctionT = JavaParser.parseClassOrInterfaceType("java.util.function.Function<EvaluationContext, Object>");

    public static FieldDeclaration of(Type type, String name, Expression initializer) {
        return new FieldDeclaration(
                EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
                new VariableDeclarator(type, name, initializer));
    }

    public static FieldDeclaration numeric(String name, String numericValue) {
        ObjectCreationExpr initializer = new ObjectCreationExpr();
        initializer.setType(BigDecimalT);
        String originalText = numericValue;
        try {
            Long.parseLong(originalText);
            initializer.addArgument(originalText.replaceFirst("^0+(?!$)", "")); // see EvalHelper.getBigDecimalOrNull
        } catch (Throwable t) {
            initializer.addArgument(new StringLiteralExpr(originalText));
        }
        initializer.addArgument(DECIMAL_128);
        return of(BigDecimalT, name, initializer);
    }

    public static String numericName(String originalText) {
        return "K_" + CodegenStringUtil.escapeIdentifier(originalText);
    }

    public static FieldDeclaration unaryTest(String name, LambdaExpr value) {
        return of(UnaryTestT, name, value);
    }

    public static String unaryTestName(String originalText) {
        return "UT_" + CodegenStringUtil.escapeIdentifier(originalText);
    }

    public static FieldDeclaration function(String name, LambdaExpr value) {
        return of(FunctionT, name, value);
    }

    public static String functionName(String originalText) {
        return "ZZFN_" + CodegenStringUtil.escapeIdentifier(originalText);
    }

    public static FieldAccessExpr rangeBoundary(RangeNode.IntervalBoundary boundary) {
        return new FieldAccessExpr(
                new NameExpr(RangeBoundary),
                boundary == RangeNode.IntervalBoundary.OPEN ? "OPEN" : "CLOSED");
    }
}
