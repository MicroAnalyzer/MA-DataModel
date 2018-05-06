package joelbits.modules.analysis.converters;

import com.google.protobuf.InvalidProtocolBufferException;
import joelbits.model.ast.*;
import joelbits.model.ast.protobuf.ASTProtos;
import joelbits.model.ast.types.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Maps a ASTRoot protocol buffer message into its ASTRoot model representation. The ASTRoot model
 * is used in the analyses performed by the framework.
 */
public final class ASTConverter {
    public ASTRoot convert(byte[] astRoot) throws InvalidProtocolBufferException {
        ASTProtos.ASTRoot root = ASTProtos.ASTRoot.parseFrom(astRoot);

        List<Namespace> namespaces = new ArrayList<>();
        for (ASTProtos.Namespace namespace : root.getNamespacesList()) {
            List<Modifier> modifiers = new ArrayList<>();
            for (ASTProtos.Modifier modifier : namespace.getModifiersList()) {
                modifiers.add(convertModifier(modifier));
            }

            List<Declaration> declarations = new ArrayList<>();
            for (ASTProtos.Declaration declaration : namespace.getDeclarationsList()) {
                declarations.add(convertDeclaration(declaration));
            }

            namespaces.add(new Namespace(namespace.getName(), declarations, modifiers));
        }

        return new ASTRoot(root.getImportsList(), namespaces);
    }

    private Modifier convertModifier(ASTProtos.Modifier modifier) {
        ModifierType type = ModifierType.valueOf(modifier.getType().name());
        VisibilityType visibilityType = VisibilityType.valueOf(modifier.getVisibility().name());

        return new Modifier(modifier.getName(), type, modifier.getMembersAndValuesList(), visibilityType, modifier.getOther());
    }

    private Expression convertExpression(ASTProtos.Expression expression) {
        String literal = expression.getLiteral();
        String method = expression.getMethod();
        String variable = expression.getVariable();
        boolean isPostfix = expression.getIsPostfix();
        ExpressionType type = ExpressionType.valueOf(expression.getType().name());
        DeclarationType newTypeType = DeclarationType.valueOf(expression.getNewType().getType().name());
        Type newType = new Type(expression.getNewType().getName(), newTypeType);

        List<Expression> methodArguments = new ArrayList<>();
        if (type.equals(ExpressionType.METHODCALL)) {
            for (ASTProtos.Expression expr : expression.getMethodArgumentsList()) {
                methodArguments.add(convertExpression(expr));
            }
        }

        List<Variable> variableDeclarations = new ArrayList<>();
        for (ASTProtos.Variable var : expression.getVariableDeclarationsList()) {
            variableDeclarations.add(convertField(var));
        }

        List<Expression> expressions = new ArrayList<>();
        for (ASTProtos.Expression exp : expression.getExpressionsList()) {
            expressions.add(convertExpression(exp));
        }

        return new Expression(type, literal, method, variable, methodArguments, variableDeclarations, isPostfix, newType, expressions);
    }

    private Declaration convertDeclaration(ASTProtos.Declaration declaration) {
        DeclarationType type = DeclarationType.valueOf(declaration.getType().name());

        List<Modifier> modifiers = new ArrayList<>();
        for (ASTProtos.Modifier modifier : declaration.getModifiersList()) {
            modifiers.add(convertModifier(modifier));
        }

        List<Type> parents = new ArrayList<>();
        for (ASTProtos.Type parent : declaration.getParentsList()) {
            parents.add(convertType(parent));
        }

        List<Variable> fields = new ArrayList<>();
        for (ASTProtos.Variable field : declaration.getFieldsList()) {
            fields.add(convertField(field));
        }

        List<Method> methods = new ArrayList<>();
        for (ASTProtos.Method method : declaration.getMethodsList()) {
            methods.add(convertMethod(method));
        }

        List<Declaration> nestedDeclarations = new ArrayList<>();
        for (ASTProtos.Declaration nestedDeclaration : declaration.getNestedDeclarationsList()) {
            nestedDeclarations.add(convertNestedDeclaration(nestedDeclaration));
        }

        return new Declaration(declaration.getName(), nestedDeclarations, modifiers, type, fields, methods, parents);
    }

    private Type convertType(ASTProtos.Type type) {
        return new Type(type.getName(), DeclarationType.valueOf(type.getType().name()));
    }

    private Variable convertField(ASTProtos.Variable field) {
        List<Modifier> modifiers = new ArrayList<>();
        for (ASTProtos.Modifier modifier : field.getModifiersList()) {
            modifiers.add(convertModifier(modifier));
        }

        return new Variable(field.getName(), convertType(field.getType()), convertInitializer(field.getInitializer()), modifiers);
    }

    private Expression convertInitializer(ASTProtos.Expression expression) {
        ExpressionType type = ExpressionType.valueOf(expression.getType().name());
        DeclarationType newTypeType = DeclarationType.valueOf(expression.getNewType().getType().name());
        Type newType = new Type(expression.getNewType().getName(), newTypeType);

        List<Expression> expressions = new ArrayList<>();
        for (ASTProtos.Expression exp : expression.getExpressionsList()) {
            expressions.add(convertExpression(exp));
        }

        return new Expression(type, expression.getLiteral(), expression.getMethod(), expression.getVariable(), new ArrayList<>(), new ArrayList<>(), expression.getIsPostfix(),newType, expressions);
    }

    private Method convertMethod(ASTProtos.Method method) {
        List<Modifier> modifiers = new ArrayList<>();
        for (ASTProtos.Modifier modifier : method.getModifiersList()) {
            modifiers.add(convertModifier(modifier));
        }

        List<Variable> arguments = new ArrayList<>();
        for (ASTProtos.Variable argument : method.getArgumentsList()) {
            arguments.add(convertField(argument));
        }

        List<Statement> statements = new ArrayList<>();
        for (ASTProtos.Statement statement : method.getStatementsList()) {
            statements.add(convertStatement(statement));
        }

        List<Expression> bodyContent = new ArrayList<>();
        for (ASTProtos.Expression expression : method.getBodyContentList()) {
            bodyContent.add(convertExpression(expression));
        }

        return new Method(method.getName(), arguments, convertType(method.getReturnType()), modifiers, bodyContent, statements);
    }

    private Statement convertStatement(ASTProtos.Statement statement) {
        StatementType type = StatementType.valueOf(statement.getType().name());

        List<Expression> expressions = new ArrayList<>();
        for (ASTProtos.Expression expression : statement.getExpressionsList()) {
            expressions.add(convertExpression(expression));
        }

        List<Expression> initializations = new ArrayList<>();
        for (ASTProtos.Expression initialization : statement.getInitializationsList()) {
            initializations.add(convertExpression(initialization));
        }

        List<Expression> updates = new ArrayList<>();
        for (ASTProtos.Expression update : statement.getUpdatesList()) {
            updates.add(convertExpression(update));
        }

        List<Statement> nestedStatements = new ArrayList<>();
        for (ASTProtos.Statement nestedStatement : statement.getStatementsList()) {
            nestedStatements.add(convertNestedStatement(nestedStatement));
        }

        Expression condition = convertExpression(statement.getCondition());
        return new Statement(type, expressions, condition, nestedStatements, initializations, updates);
    }

    private Declaration convertNestedDeclaration(ASTProtos.Declaration declaration) {
        DeclarationType type = DeclarationType.valueOf(declaration.getType().name());

        List<Modifier> modifiers = new ArrayList<>();
        for (ASTProtos.Modifier modifier : declaration.getModifiersList()) {
            modifiers.add(convertModifier(modifier));
        }

        List<Type> parents = new ArrayList<>();
        for (ASTProtos.Type parent : declaration.getParentsList()) {
            parents.add(convertType(parent));
        }

        List<Variable> fields = new ArrayList<>();
        for (ASTProtos.Variable field : declaration.getFieldsList()) {
            fields.add(convertField(field));
        }

        List<Method> methods = new ArrayList<>();
        for (ASTProtos.Method method : declaration.getMethodsList()) {
            methods.add(convertMethod(method));
        }

        return new Declaration(declaration.getName(), new ArrayList<>(), modifiers, type, fields, methods, parents);
    }

    private Statement convertNestedStatement(ASTProtos.Statement statement) {
        StatementType type = StatementType.valueOf(statement.getType().name());

        List<Expression> expressions = new ArrayList<>();
        for (ASTProtos.Expression expression : statement.getExpressionsList()) {
            expressions.add(convertExpression(expression));
        }

        List<Expression> initializations = new ArrayList<>();
        for (ASTProtos.Expression initialization : statement.getInitializationsList()) {
            initializations.add(convertExpression(initialization));
        }

        List<Expression> updates = new ArrayList<>();
        for (ASTProtos.Expression update : statement.getUpdatesList()) {
            updates.add(convertExpression(update));
        }

        List<Statement> nestedStatements = new ArrayList<>();
        for (ASTProtos.Statement nestedStatement : statement.getStatementsList()) {
            nestedStatements.add(convertNestedNestedStatement(nestedStatement));
        }

        Expression condition = convertExpression(statement.getCondition());

        return new Statement(type, expressions, condition, nestedStatements, initializations, updates);
    }

    private Statement convertNestedNestedStatement(ASTProtos.Statement statement) {
        StatementType type = StatementType.valueOf(statement.getType().name());

        List<Expression> expressions = new ArrayList<>();
        for (ASTProtos.Expression expression : statement.getExpressionsList()) {
            expressions.add(convertExpression(expression));
        }

        List<Expression> initializations = new ArrayList<>();
        for (ASTProtos.Expression initialization : statement.getInitializationsList()) {
            initializations.add(convertExpression(initialization));
        }

        List<Expression> updates = new ArrayList<>();
        for (ASTProtos.Expression update : statement.getUpdatesList()) {
            updates.add(convertExpression(update));
        }

        Expression condition = convertExpression(statement.getCondition());

        return new Statement(type, expressions, condition, Collections.emptyList(), initializations, updates);
    }
}
