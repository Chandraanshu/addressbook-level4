package seedu.address.logic.parser;

import java.util.*;

/**
 * Parses arguments string of the form: non-prefix-args <prefix>value <prefix>value ...
 * An argument's value is assumed to not contain its prefix and any leading or trailing
 * whitespaces will be discarded. Argument may be allowed to be repeated or not. If a
 * non-repeatable argument is repeated, its last value will take precedence.
 */
public class CommandTokenizer {
    /**
     * Describes an argument to be parsed. Each argument should have a unique
     * name and prefix and a prefix should not be a substring of any other prefixes.
     * Otherwise, behaviour is undefined.
     */
    public abstract static class Argument {
        protected final String name;

        public Argument(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public static class NonPrefixedArgument extends Argument {
        public NonPrefixedArgument(String name) {
            super(name);
        }
    }

    public abstract static class PrefixedArgument extends Argument {
        private final String prefix;

        public PrefixedArgument(String name, String prefix) {
            super(name);
            this.prefix = prefix;
        }

        public String getPrefix() {
            return this.prefix;
        }
    }

    public static class RepeatableArgument extends PrefixedArgument {
        public RepeatableArgument(String name, String prefix) {
            super(name, prefix);
        }
    }

    public static class NonRepeatableArgument extends PrefixedArgument {
        public NonRepeatableArgument(String name, String prefix) {
            super(name, prefix);
        }
    }

    public static class ParsedArguments {
        private String nonPrefixArgument = "";
        private Map<String, String> nonRepeatableArguments = new HashMap<>();
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
            return addNonRepeatableArgument(argument.name, value);
        }

        public void addNonPrefixArgument(String value) {
            this.nonPrefixArgument = value;
        }

        /**
         * @param name name of the argument
         * @param value value of the argument
         * @return false if the argument already exists. New value overrides old one
         */
        private boolean addNonRepeatableArgument(String name, String value) {
            boolean isExisted = this.nonRepeatableArguments.containsKey(name);
            this.nonRepeatableArguments.put(name, value);
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

        public Optional<String> getArgumentValue(NonRepeatableArgument argument) {
            if (!this.nonRepeatableArguments.containsKey(argument.getName())) {
                return Optional.empty();
            }
            return Optional.of(this.nonRepeatableArguments.get(argument.getName()));
        }

        public Optional<List<String>> getArgumentValue(RepeatableArgument argument) {
            if (!this.repeatableArguments.containsKey(argument.getName())) {
                return Optional.empty();
            }
            return Optional.of(this.repeatableArguments.get(argument.getName()));
        }

        public Optional<String> getArgumentValue(NonPrefixedArgument argument) {
            if (this.nonPrefixArgument.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(this.nonPrefixArgument);
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
    public CommandTokenizer(List<Argument> arguments) {
        this.arguments = arguments;
        this.parsingData = new ArrayList<>();
        this.parsedArguments = new ParsedArguments();
    }

    /**
     * @param argumentsString arguments string of the form: non-prefix-args <prefix>data <prefix>data ...
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

        ArgumentParsingData firstArg = this.parsingData.get(0);
        String value = "";
        if (firstArg.startPos > 0) {
            value = argumentsString.substring(0, firstArg.startPos).trim();
        }
        if (!value.isEmpty()) {
            this.parsedArguments.addNonPrefixArgument(value);
        }

        for (int i = 0; i < this.parsingData.size() - 1; i++) {
            ArgumentParsingData currentArg = this.parsingData.get(i);
            ArgumentParsingData nextArg = this.parsingData.get(i + 1);
            value = argumentsString.substring(currentArg.startPos + currentArg.prefix.length(), nextArg.startPos);
            this.parsedArguments.addArgument(currentArg, value.trim());
        }

        ArgumentParsingData lastArg = this.parsingData.get(this.parsingData.size() - 1);
        value = argumentsString.substring(lastArg.startPos + lastArg.prefix.length());
        this.parsedArguments.addArgument(lastArg, value.trim());
    }
}
