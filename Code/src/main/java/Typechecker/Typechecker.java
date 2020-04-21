package Typechecker;

import Parser.Expressions.*;
import Parser.Declarations.Decl;
import Parser.Parser;
import Parser.Statements.Stmt;
import Parser.Literals.*;
import Tokenizer.Tokenizer;
import Typechecker.Types.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

public class Typechecker {

    private static class Pair<U, V> {
        public final U first;
        public final V second;
        public Pair(final U first, final V second) {
            this.first = first;
            this.second = second;
        }
        public String toString() {
            return String.format("(" + first + ", " + second + ")");
        }
    }


    /**
     * creates a copy of an ImmutableMap and adds new mappings
     * @param gamma current mapping
     * @param pairs new key-value pairs to add to map
     * @return copy of gamma with new mappings
     */
    private static ImmutableMap<String, Type> addToGamma(
            final ImmutableMap<String, Type> gamma, final Pair<String, Type>... pairs) {

        Map<String, Type> newMappings = new HashMap<>();
        for (final Pair curPair : pairs) {
            newMappings.put((String)curPair.first, (Type)curPair.second);
        }

        final ImmutableMap<String, Type> newGamma = ImmutableMap.<String, Type>builder()
                .putAll(gamma)
                .putAll(newMappings)
                .build();

        return newGamma;
    }

    /**
     * attempts to typecheck an expression
     * @param gamma map of bound variables
     * @param e current expression
     * @return type of e
     * @throws IllTypedException unrecognized expression
     */
    public static Type typeof(final ImmutableMap<String, Type> gamma, final Exp e) throws IllTypedException{

        if (e instanceof BinaryOperatorExp) {
            final BinaryOperatorExp asBOP = (BinaryOperatorExp)e;
            final Type left = typeof(gamma, asBOP.left);
            final Type right = typeof(gamma, asBOP.right);

            //TODO string concat?
            if (asBOP.op.matches("[-+*/]")) {
                if (left instanceof IntType && right instanceof IntType) {
                    return new IntType();
                }
                else {
                    throw new IllTypedException("Operator " + asBOP.op +
                            " cannot be applied to " + left +", " + right);
                }
            }
            else if (asBOP.op.matches("[<>]")){
                if (left instanceof IntType && right instanceof IntType) {
                    return new BoolType();
                } else {
                    throw new IllTypedException("Operator " + asBOP.op +
                            " cannot be applied to " + left +", " + right);
                }
            }
            else if (asBOP.op.equals("!=") || asBOP.op.equals("==")) {
                if (left instanceof IntType && right instanceof IntType) {
                    return new BoolType();
                }
                if (left instanceof BoolType && right instanceof BoolType) {
                    return new BoolType();
                } else {
                    throw new IllTypedException("Operator " + asBOP.op +
                            " cannot be applied to " + left +", " + right);
                }
            }
            else {
                assert(false);
                throw new IllTypedException("Illegal binary operation");
            }
        }

        else if (e instanceof PreIncrDecrExp) {
            final PreIncrDecrExp asPre = (PreIncrDecrExp)e;
            final Type expType = typeof(gamma, asPre.prefixExp);
            if (expType instanceof IntType)
                return expType;
            else
                throw new IllTypedException("Cannot apply ++/-- on type " + expType);
        }

        else if (e instanceof PostIncrDecrExp) {
            final PostIncrDecrExp asPost = (PostIncrDecrExp) e;
            final Type expType = typeof(gamma, asPost.postfixExp);
            if (expType instanceof IntType)
                return expType;
            else
                throw new IllTypedException("Cannot apply ++/-- on type " + expType);
        }
        else if (e instanceof NegateUnaryExp) {
            final NegateUnaryExp asNeg = (NegateUnaryExp) e;
            final Type expType = typeof(gamma, asNeg.exp);
            if (expType instanceof BoolType)
                return expType;
            else
                throw new IllTypedException("Cannot negate a non-boolean type " + expType);
        }
        else if (e instanceof IntegerLiteral) {
            return new IntType();
        }
        else if (e instanceof BooleanLiteral) {
            return new BoolType();
        }
        else if (e instanceof StringLiteral) {
            return new StringType();
        }
        else if (e instanceof NullLiteral) {
            return new NullType();
        }
        //have to check if the variable is in scope
        else if (e instanceof IdentifierLiteral) {
            IdentifierLiteral asID = (IdentifierLiteral)e;
            if (gamma.containsKey(asID.name)) {
                return gamma.get(asID.name);
            } else {
                throw new IllTypedException("Variable not in scope: " + asID.name);
            }
        }
        else {
            assert(false);
            throw new IllTypedException("unrecognized expression: " + e.toString());
        }
    }
    public static void main(String[] args) {

        Map<String, Type> test = new HashMap<>();
        test.put("foo", new BoolType());

        ImmutableMap gamma = ImmutableMap.copyOf(test);
        ImmutableMap newGamma = addToGamma(gamma, new Pair("y", new IntType()));

        try{
            System.out.println(typeof(null, new IdentifierLiteral("foo")));
        } catch (IllTypedException e) {
            e.printStackTrace();
        }
        System.out.println(gamma);
        System.out.println(newGamma);
        try {
            File file = new File("testProgram.odang");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String tokenizerInput = "";
            String line = "";
            while ((line = br.readLine()) != null) {
                tokenizerInput += line;
            }
            br.close();
            final Tokenizer tokenizer = new Tokenizer(tokenizerInput);
            final Parser parser = new Parser(tokenizer.tokenize());
            final List<Decl> parsed = parser.parseProgram();

            System.out.println(parsed);

            System.out.println(typeof(null, new BooleanLiteral(true)));

        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() +
                    ": " + e.getMessage());
        }

    }

}
