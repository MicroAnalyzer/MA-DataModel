package joelbits.model.ast;

import joelbits.modules.analysis.visitors.Visitor;
import joelbits.model.ast.types.StatementType;

import java.util.ArrayList;
import java.util.List;

public final class Statement implements ASTNode {
    private final StatementType type;
    private final List<Expression> expressions;         // other expressions such as the Compare part of a For loop, or method return value expressions
    private final Expression condition;                 // Various conditions such as a Do loops condition, or an If statements condition, or a While loops condition
    private final List<Statement> statements;           // Other statements occurring inside this statement. Could be the content of a Try body, or an Else body, or a While loops body
    private final List<Expression> initializations;     // Could be the initialization part of a For loop
    private final List<Expression> updates;             // Update expressions such as the update part of a For loop

    public Statement(StatementType type, List<Expression> expressions, Expression condition, List<Statement> statements, List<Expression> initializations, List<Expression> updates) {
        this.type = type;
        this.expressions = new ArrayList<>(expressions);
        this.condition = condition;
        this.statements = new ArrayList<>(statements);
        this.initializations = new ArrayList<>(initializations);
        this.updates = new ArrayList<>(updates);
    }

    /**
     *  The ASTNode's accept implementation uses the answer from visitEnter to determine whether its children
     *  should accept this visitor. So, if visitEnter answers true, accept is invoked on each of its children
     *  or until one of the accept invocations answers false. Once a parent node has called accept for each of
     *  its children, it will call visitor.visitLeave. This lets the visitor know it is done with this branch
     *  and proceeding to either a sibling or parent ASTNode at the same tree-depth as this node.
     *
     * @param visitor
     * @return          true if proceed with a sibling ASTNode, false if not
     */
    @Override
    public boolean accept(Visitor visitor) {
        if (visitor.visitEnter(this)) {
            for (Expression expression : expressions) {
                if (!expression.accept(visitor)) {
                    break;
                }
            }
            for (Expression expression : initializations) {
                if (!expression.accept(visitor)) {
                    break;
                }
            }
            for (Expression expression : updates) {
                if (!expression.accept(visitor)) {
                    break;
                }
            }
            condition.accept(visitor);
            for (Statement statement : statements) {
                if (!statement.accept(visitor)) {
                    break;
                }
            }
        }

        return visitor.visitLeave(this);
    }

    public StatementType getType() {
        return type;
    }

    public List<Expression> getExpressions() {
        return new ArrayList<>(expressions);
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Statement> getStatements() {
        return new ArrayList<>(statements);
    }

    public List<Expression> getInitializations() {
        return new ArrayList<>(initializations);
    }

    public List<Expression> getUpdates() {
        return new ArrayList<>(updates);
    }
}
