package Typechecker;

import Parser.Declarations.*;
import Parser.Literals.*;
import Typechecker.Types.*;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeEnvironment {
    public final ImmutableMap<FunctionName,FunctionDefinition> functions;
    public final ImmutableMap<String, Type> variables;
    public final String thisClass;

    public TypeEnvironment(final ImmutableMap<FunctionName, FunctionDefinition> functions,
                           final ImmutableMap<String, Type> variables,
                           final String thisClass) {

        this.functions = (functions == null) ?
                ImmutableMap.copyOf(new HashMap<FunctionName, FunctionDefinition>()) : functions;
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

    public FunctionDefinition lookupFunction(final FunctionName functionName) throws IllTypedException {
        final FunctionDefinition result = functions.get(functionName);
        if (result == null)
            throw new IllTypedException("No such function defined: " + functionName);
        else
            return result;
    }
    public TypeEnvironment addFunction(final FunctionName functionName,
                                       final FunctionDefinition functionDefinition) {
        final Map<FunctionName, FunctionDefinition> newFunctions = new HashMap<>(functions);
        newFunctions.put(functionName, functionDefinition);
        return new TypeEnvironment(ImmutableMap.copyOf(newFunctions), variables, thisClass);
    }

    public TypeEnvironment addVariable(final String variable, final Type type) {
        final Map<String, Type> newVariables = new HashMap<>(variables);
        newVariables.put(variable, type);
        return new TypeEnvironment(functions, ImmutableMap.copyOf(newVariables), thisClass);
    }

    public TypeEnvironment addVariable(final FieldDecl fieldDecl) throws IllTypedException {
        final VarDeclaratorList varDeclarators = (VarDeclaratorList) fieldDecl.varDeclarators;
        final Type type = Typechecker.convertParserType(fieldDecl.parserType);

        final Map<String, Type> newVariables = new HashMap<>(variables);

        for (final Decl decl : varDeclarators.varDeclList) {
            final VarDeclarator varDec = (VarDeclarator) decl;
            final IdentifierLiteral identifier = (IdentifierLiteral) varDec.identifier;
            newVariables.put(identifier.name, type);
        }

        final ImmutableMap<String, Type> finalVariables = ImmutableMap
                .<String, Type>builder()
                .putAll(variables)
                .putAll(newVariables)
                .build();

        return new TypeEnvironment(functions, finalVariables, thisClass);
    }

}
