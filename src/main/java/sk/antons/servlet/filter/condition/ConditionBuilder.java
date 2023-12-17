/*
 *
 */
package sk.antons.servlet.filter.condition;

import java.util.Stack;
import sun.nio.cs.HKSCSMapping;

/**
 *
 * @author antons
 */
public class ConditionBuilder<C> {

    Stack<StackEntry> stack = new Stack<>();
    StringBuilder path = new StringBuilder();

    private ConditionBuilder() {
        stack.push(StackEntry.of(Type.Full));
    }

    public static <T> ConditionBuilder<T> instance(Class<T> clazz) { return new ConditionBuilder(); }

    public Condition<C> condition() {
        if(stack.size() != 1) throw new IllegalStateException("bad condition format at " + path);
        if(stack.peek().condition == null) throw new IllegalStateException("bad condition format at " + path);
        return (Condition<C>)stack.peek().condition;
    }

    public ConditionBuilder<C> add(Condition<C> condition) {
        path.append(' ').append(condition);
        if(stack.peek().condition != null) throw new IllegalStateException("bad condition format at " + path);
        stack.peek().condition = condition;
        reduceStack(false);
        return this;
    }

    public ConditionBuilder<C> not() {
        path.append(" not");
        if(stack.peek().condition != null) throw new IllegalStateException("bad condition format at " + path);
        stack.push(StackEntry.of(Type.Not));
        return this;
    }

    public ConditionBuilder<C> and() {
        path.append(" and");
        if(stack.peek().condition == null) throw new IllegalStateException("bad condition format at " + path);
        stack.push(StackEntry.of(Type.And));
        return this;
    }

    public ConditionBuilder<C> or() {
        path.append(" or");
        System.out.println(" --stack " + stack);
        if(stack.peek().condition == null) throw new IllegalStateException("bad condition format at " + path);
        stack.push(StackEntry.of(Type.Or));
        return this;
    }

    public ConditionBuilder<C> lb() {
        path.append(" (");
        stack.push(StackEntry.of(Type.Full));
        return this;
    }

    public ConditionBuilder<C> rb() {
        path.append(" )");
        if(stack.peek().type != Type.Full) throw new IllegalStateException("bad condition format at " + path);
        if(stack.peek().condition == null) throw new IllegalStateException("bad condition format at " + path);
        reduceStack(true);
        return this;
    }

    private void reduceStack(boolean force) {
        if(stack.size() == 1) return;
        if(stack.peek().condition == null) return;
        StackEntry top = stack.pop();
        if(top.type == Type.Not) {
            stack.peek().condition = NotCondition.instance(top.condition);
            reduceStack(false);
        } else if(top.type == Type.And) {
            stack.peek().condition = AndCondition.instance((Condition<C>)stack.peek().condition, (Condition<C>)top.condition);
            reduceStack(false);
        } else if(top.type == Type.Or) {
            stack.peek().condition = OrCondition.instance((Condition<C>)stack.peek().condition, (Condition<C>)top.condition);
            reduceStack(false);
        } else if(top.type == Type.Full) {
            if(force) {
                if(stack.peek().condition == null) {
                    stack.peek().condition = top.condition;
                    reduceStack(false);
                }
            } else {
                stack.push(top);
            }
        } else {
            throw new IllegalStateException("bad condition format at " + path);
        }
    }

    private static enum Type {
        Full, Not, And, Or;
    }

    private static class StackEntry {
        Condition<?> condition = null;
        Type type = null;

        public static StackEntry of(Type type) {
            StackEntry entry = new StackEntry();
            entry.type = type;
            return entry;
        }

        @Override
        public String toString() {
            return type + "{" + condition + '}';
        }

    }

}
