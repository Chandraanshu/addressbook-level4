package seedu.address.logic.parser;

import java.util.*;

/**
 * Parses arguments string of the form: <prefix>value <prefix>value ...
 * An argument's value is assumed to not contain its prefix and any leading or trailing
 * whitespaces will be discarded. Argument may be allowed to be repeated or not. If a
 * non-repeatable argument is repeated, its last value will take precedence.
 */
public class ArgumentsParser {
    /**
     * Describes an argument to be parsed. Each argument should have a unique
     * name and prefix and a prefix should not be a substring of any other prefixes.
     * Otherwise, behaviour is undefined.
     */
    public static class Argument {
        private final String name;
        private final String prefix;
        private boolean isRepeatable;

        public Argument(String name, String prefix) {
            this.name = name;
            this.prefix = prefix;
            this.isRepeatable = false;
        }

        public Argument(String name, String prefix, boolean isRepeatable) {
            this(name, prefix);
            this.isRepeatable = isRepeatable;
        }
    }

    public static class ParsedArguments {
        Map<String, String> onceArguments = new HashMap<>();
        Map<String, List<String>> repeatableArguments = new HashMap<>();

        /**
         * Adds an non-repeatable argument parsed result
         * @param name name of the argument
         * @param value value of the argument
         * @return false if the argument already exists. New value will overrides old one
         */
        public boolean addOnceArgument(String name, String value) {
            boolean isExisted = this.onceArguments.containsKey(name);
            this.onceArguments.put(name, value);
            return isExisted;
        }

        /**
         * Adds an repeatable argument parsed result
         * @param name name of the argument
         * @param value value of the argument
         */
        public void addRepeatableArgument(String name, String value) {
            if (this.repeatableArguments.containsKey(name)) {
                this.repeatableArguments.get(name).add(value);
                return;
            }
            List<String> argumentValues = new ArrayList<>();
            this.repeatableArguments.put(name, argumentValues);
        }

        /**
         * Gets the value of a non-repeatable argument
         * @param name name of the argument
         */
        public Optional<String> getOnceArgument(String name) {
            try {
                return Optional.of(this.onceArguments.get(name));
            } catch (NullPointerException e) {
                return Optional.empty();
            }
        }

        /**
         * Gets the value of a repeatable argument
         * @param name name of the argument
         */
        public Optional<List<String>> getRepeatableArgument(String name) {
            try {
                return Optional.of(this.repeatableArguments.get(name));
            } catch (NullPointerException e) {
                return Optional.empty();
            }
        }
    }

    /**
     * Intermediate data for an argument during parsing phase
     */
    private class ArgumentParsingData extends Argument {
        public int startPos;

        public ArgumentParsingData(Argument argument, int startPos) {
            super(argument.name, argument.prefix, argument.isRepeatable);
            this.startPos = startPos;
        }
    }

    private final List<Argument> arguments;
    private List<ArgumentParsingData> parsingData;
    private final ParsedArguments parsedArguments;

    /**
     * Creates an ArgumentParser that can parse arguments string as described
     * by the `arguments` list
     * @param arguments
     */
    public ArgumentsParser(List<Argument> arguments) {
        this.arguments = arguments;
        parsingData = new ArrayList<>();
        parsedArguments = new ParsedArguments();
    }

    /**
     * @param argumentsString arguments string of the form <prefix>data <prefix>data ...
     */
    public ParsedArguments parse(String argumentsString) {
        for (Argument argument : this.arguments) {
            extractArgumentParsingData(argumentsString, argument);
        }
        parsingData.sort((arg1, arg2) -> arg1.startPos - arg2.startPos);
        return this.parsedArguments;
    }

    /**
     * Extracts the values of each argument and store them in `parsedArguments`.
     * This method requires `parsingData` to be fully filled and sorted according to
     * each argument starting position.
     */
    private void extractArgumentValues() {

    }

    private void extractArgumentParsingData(String argumentsString, Argument argument) {
        int argumentStart = argumentsString.indexOf(argument.prefix);
        while (argumentStart != -1) {
            ArgumentParsingData argumentParsingData =
                    new ArgumentParsingData(argument, argumentStart);
            parsingData.add(argumentParsingData);
            argumentStart = argumentsString.indexOf(argument.prefix, argumentStart + 1);
        }
    }
}
