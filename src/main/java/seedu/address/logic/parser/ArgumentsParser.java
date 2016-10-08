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
     * name and prefix. Otherwise, behaviour is undefined.
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
        Map<String, String> onceArguments = new HashMap<String, String>();
        Map<String, List<String>> repeatableArguments = new HashMap<String, List<String>>();

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

        public void addRepeatableArgument(String name, String value) {
            if (this.repeatableArguments.containsKey(name)) {
                this.repeatableArguments.get(name).add(value);
                return;
            }
            List<String> argumentValues = new ArrayList<>();
            this.repeatableArguments.put(name, argumentValues);
        }

        public Optional<String> getOnceArgument(String name) {
            try {
                return Optional.of(this.onceArguments.get(name));
            } catch (NullPointerException e) {
                return Optional.empty();
            }
        }

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
    private class ArgumentParsingData {
        public String name;
        public int startPos;
        public boolean isRepeatable;

        public ArgumentParsingData(String name, int startPos, boolean isRepeatable) {
            this.name = name;
            this.startPos = startPos;
            this.isRepeatable = isRepeatable;
        }
    }

    private final List<Argument> arguments;
    private List<ArgumentParsingData> parsingData;
    private final ParsedArguments parsedArguments;

    public ArgumentsParser(List<Argument> arguments) {
        this.arguments = arguments;
        parsingData = new ArrayList<>();
        parsedArguments = new ParsedArguments();
    }
}
