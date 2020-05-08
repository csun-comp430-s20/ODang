package Typechecker;

import Typechecker.Types.*;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class TypeEnvironment {
    private final ImmutableMap<String,FunctionDefinition> functions;
    private final ImmutableMap<String, Type> variables;
    private final String thisClass;

    public TypeEnvironment(final ImmutableMap<String, FunctionDefinition> functions,
                           final ImmutableMap<String, Type> variables,
                           final String thisClass) {

        this.functions = (functions == null) ?
                ImmutableMap.copyOf(new HashMap<String, FunctionDefinition>()) : functions;
        this.variables = (variables == null) ?
                ImmutableMap.copyOf(new HashMap<String, Type>()) : variables;
        this.thisClass = thisClass;
    }

    public Type thisType() throws IllTypedException {
        if (thisClass == null)
            throw new IllTypedException("this used outside of class");
        else {
            return new ClassType(thisClass);
        }
    }

    public Type lookupVariable(final String variable) throws IllTypedException {
        final Type result = variables.get(variable);
        if (result == null)
            throw new IllTypedException("No such variable defined: " + variable);
        else
            return result;
    }

    public FunctionDefinition lookupFunction(final String functionName) throws IllTypedException {
        final FunctionDefinition result = functions.get(functionName);
        if (result == null)
            throw new IllTypedException("No such function defined: " + functionName);
        else
            return result;
    }
    public TypeEnvironment addFunction(final String functionName,
                                       final FunctionDefinition functionDefinition) {
        final Map<String, FunctionDefinition> newFunctions = new HashMap<>(functions);
        newFunctions.put(functionName, functionDefinition);
        return new TypeEnvironment(ImmutableMap.copyOf(newFunctions), variables, thisClass);
    }

    public TypeEnvironment addVariable(final String variable, final Type type) throws IllTypedException {
        final Map<String, Type> newVariables = new HashMap<>(variables);
        if (newVariables.containsKey(variable))
            throw new IllTypedException("Variable already defined: " + variable);

        newVariables.put(variable, type);
        return new TypeEnvironment(functions, ImmutableMap.copyOf(newVariables), thisClass);
    }

    public boolean containsVariable(final String name) {
        return variables.containsKey(name);
    }

    public boolean containsFunction(final String name) {
        return functions.containsKey(name);
    }

    public boolean variablesIsEmpty() {
        return variables.isEmpty();
    }

    public boolean functionsIsEmpty() {
        return functions.isEmpty();
    }

    public TypeEnvironment copy() {
        return new TypeEnvironment(
                functions,
                variables,
                thisClass);
    }

    public ImmutableMap<String, Type> getVariables() {
        return variables;
    }

    public ImmutableMap<String, FunctionDefinition> getFunctions() {
        return functions;
    }

    public String getClassName() throws IllTypedException {
        if (thisClass == null)
            throw new IllTypedException("No class associated to this environment");
        else
            return thisClass;
    }

    public String toString() {
        return String.format("Class: " + thisClass +
                "\nFunctions: " + functions +
                "\nVariables: " + variables);
    }
}
