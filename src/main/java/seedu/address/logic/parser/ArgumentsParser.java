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
        protected final String name;
        protected final String prefix;
        protected boolean isRepeatable;

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
        private Map<String, String> onceArguments = new HashMap<>();
        private Map<String, List<String>> repeatableArguments = new HashMap<>();

        /**
         * Adds the result for an argument
         * @param argument
         * @param value value of the argument
         * @return true if the argument is non-repeatable and already exists
         */
        public boolean addArgument(Argument argument, String value) {
            if (argument.isRepeatable) {
                addRepeatableArgument(argument.name, value);
                return true;
            }
            return addOnceArgument(argument.name, value);
        }

        /**
         * @param name name of the argument
         * @param value value of the argument
         * @return false if the argument already exists. New value overrides old one
         */
        private boolean addOnceArgument(String name, String value) {
            boolean isExisted = this.onceArguments.containsKey(name);
            this.onceArguments.put(name, value);
            return isExisted;
        }

        /**
         * @param name name of the argument
         * @param value value of the argument
         */
        private void addRepeatableArgument(String name, String value) {
            if (this.repeatableArguments.containsKey(name)) {
                this.repeatableArguments.get(name).add(value);
                return;
            }
            List<String> argumentValues = new ArrayList<>();
            argumentValues.add(value);
            this.repeatableArguments.put(name, argumentValues);
        }

        public Optional<String> getOnceArgumentValue(Argument argument) {
            if (!this.onceArguments.containsKey(argument.name)) {
                return Optional.empty();
            }
            return Optional.of(this.onceArguments.get(argument.name));
        }

        public Optional<List<String>> getRepeatableArgumentValue(Argument argument) {
            if (!this.repeatableArguments.containsKey(argument.name)) {
                return Optional.empty();
            }
            return Optional.of(this.repeatableArguments.get(argument.name));
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
        this.parsingData = new ArrayList<>();
        this.parsedArguments = new ParsedArguments();
    }

    /**
     * @param argumentsString arguments string of the form <prefix>data <prefix>data ...
     */
    public ParsedArguments parse(String argumentsString) {
        for (Argument argument : this.arguments) {
            extractArgumentParsingData(argumentsString, argument);
        }
        parsingData.sort((arg1, arg2) -> arg1.startPos - arg2.startPos);
        extractArgumentValues(argumentsString);
        return this.parsedArguments;
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

    /**
     * Extracts the values of each argument and store them in `parsedArguments`.
     * This method requires `parsingData` to be fully filled and sorted according to
     * each argument starting position.
     */
    private void extractArgumentValues(String argumentsString) {
        if (parsingData.isEmpty()) {
            return;
        }

        for (int i = 0; i < this.parsingData.size() - 1; i++) {
            ArgumentParsingData currentArg = this.parsingData.get(i);
            ArgumentParsingData nextArg = this.parsingData.get(i + 1);
            String value = argumentsString.substring(currentArg.startPos + currentArg.prefix.length(),
                                                     nextArg.startPos);
            this.parsedArguments.addArgument(currentArg, value.trim());
        }

        ArgumentParsingData lastArg = this.parsingData.get(this.parsingData.size() - 1);
        String value = argumentsString.substring(lastArg.startPos + lastArg.prefix.length());
        this.parsedArguments.addArgument(lastArg, value.trim());
    }
}
