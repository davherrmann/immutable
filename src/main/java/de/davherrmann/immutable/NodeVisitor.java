package de.davherrmann.immutable;

@FunctionalInterface
public interface NodeVisitor
{
    // visit("nestedPath.key", value)
    void visit(String path, Object value);
}
