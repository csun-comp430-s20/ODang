package Typechecker;

import Parser.Declarations.*;
import Parser.Literals.*;
import Typechecker.Types.ClassType;
import Typechecker.Types.Type;
import Typechecker.Typechecker.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeEnvironment {

    private final Map<String, Type> variables;
    private final Map<Pair<String, FormalParamList>, Type> methods;
    private final String thisClass;

    public TypeEnvironment(final Map<String, Type> variables,
                           final Map<Pair<String, FormalParamList>, Type> methods,
                           final String thisClass) {
        this.variables = variables;
        this.methods = methods;
        this.thisClass = thisClass;
    }

    public Type thisType() throws IllTypedException {
        if(thisClass == null) {
            throw new IllTypedException("this used outside of class");
        } else {
            return new ClassType(thisClass);
        }
    }

    public Type lookupVariable(final String variable) throws IllTypedException {
        final Type result = variables.get(variable);

        if (result == null)
            throw new IllTypedException("no such variable: " + variable);
        else
            return result;
    }
    public Type getMethodType(final String methodName, final FormalParamList params) throws IllTypedException {
        if (methods.containsKey(new Pair(methodName, params))) {
            return methods.get(new Pair(methodName, params));
        }
        else
            throw new IllTypedException("No such method defined in class: " + methodName);
    }
    public void checkParamsOk(final FormalParamList paramDefinition,
                              final List<Decl> methodCallParams) throws IllTypedException {

        if (paramDefinition.declList.size() == methodCallParams.size()) {
            for (int i = 0; i < methodCallParams.size(); i++) {
                final FormalParam formalParamDef = (FormalParam) paramDefinition.declList.get(i);
                final FormalParam methodCallParam = (FormalParam) methodCallParams.get(i);
                if (formalParamDef.paramParserType.equals(methodCallParam.paramParserType)) {
                    continue;
                }
                else {
                    throw new IllTypedException("Type mismatch in method call \n " +
                            "Expected: " + formalParamDef.paramParserType + "\n" +
                            "Received: " + methodCallParam.paramParserType);
                }
            }
        }
        else {
            throw new IllTypedException("No method defined with with this param length");
        }
    }
    public TypeEnvironment addMethod(final Type returnType,
                                     final String methodName,
                                     final FormalParamList params) throws IllTypedException {
        if (!methods.containsKey(new Pair(methodName, params))) {
            TypeEnvironment newTypeEnvironment = new TypeEnvironment(variables, methods, thisClass);
            newTypeEnvironment.methods.put(new Pair(methodName, params), returnType);
            return newTypeEnvironment;
        }
        else
            throw new IllTypedException("Duplicate method definition: " + methodName);
    }

    public TypeEnvironment addVariable(final String variable, final Type type) throws IllTypedException {
        if (!variables.containsKey(variable)) {
            final Map<String, Type> newVariables = new HashMap<>(variables);
            newVariables.put(variable, type);
            return new TypeEnvironment(newVariables, methods, thisClass);
        }
        else
            throw new IllTypedException("Redefinition of variable: " + variable);
    }

    public TypeEnvironment addVariable(final FieldDecl fieldDecl) throws IllTypedException {
        final Type type = Typechecker.convertParserType(fieldDecl.parserType);
        final VarDeclaratorList vardecs = (VarDeclaratorList)fieldDecl.varDeclarators;
        TypeEnvironment newTypeEnvironment = new TypeEnvironment(variables, methods, thisClass);
        for (final Decl decl : vardecs.varDeclList) {
            final VarDeclarator vardec = (VarDeclarator)decl;
            if (!variables.containsKey(((IdentifierLiteral)vardec.identifier).name))
                newTypeEnvironment.variables.put(((IdentifierLiteral)vardec.identifier).name, type);
        }
        return newTypeEnvironment;
    }
}
