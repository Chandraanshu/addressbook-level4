package seedu.address.logic.parser;

import org.junit.BeforeClass;
import seedu.address.logic.parser.CommandTokenizer.ParsedArguments;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommandTokenizerTest {
    /**
     * Makes sure that static members of Parser are initialized
     */
    @BeforeClass
    public static void initParser() {
        Parser parser = new Parser();
    }

    @Test
    public void parse_validAddCmdArgsNoTags() {
        CommandTokenizer argsParser = new CommandTokenizer(Parser.addCmdArgs);
        ParsedArguments result = argsParser.parse(
                "John Doe p/98765432 e/johnd@gmail.com a/John street, block 123, #01-01");

        assertEquals("John Doe", result.getNonPrefixArgument().get());
        assertEquals("98765432", result.getNonRepeatableArgumentValue(Parser.phoneNumberArg).get());
        assertEquals("johnd@gmail.com", result.getNonRepeatableArgumentValue(Parser.emailArg).get());
        assertEquals("John street, block 123, #01-01", result.getNonRepeatableArgumentValue(Parser.addressArg).get());
    }

    @Test
    public void parse_validAddCmdArgsWithTags() {
        CommandTokenizer argsParser = new CommandTokenizer(Parser.addCmdArgs);
        CommandTokenizer.ParsedArguments result = argsParser.parse(
                "Betsy Crowe p/1234567 e/betsycrowe@gmail.com a/Newgate Prison t/criminal t/friend");
        List<String> tags = new ArrayList<>();
        tags.add("criminal");
        tags.add("friend");

        assertEquals("Betsy Crowe", result.getNonPrefixArgument().get());
        assertEquals("1234567", result.getNonRepeatableArgumentValue(Parser.phoneNumberArg).get());
        assertEquals("betsycrowe@gmail.com", result.getNonRepeatableArgumentValue(Parser.emailArg).get());
        assertEquals("Newgate Prison", result.getNonRepeatableArgumentValue(Parser.addressArg).get());
        assertEquals(tags, result.getRepeatableArgumentValue(Parser.tagArgs).get());
    }

    @Test
    public void parse_validAddCmdArgsChangeOrder() {
        CommandTokenizer argsParser = new CommandTokenizer(Parser.addCmdArgs);
        ParsedArguments result = argsParser.parse(
                "John Doe e/johnd@gmail.com a/John street, block 123, #01-01 p/98765432");

        assertEquals("John Doe", result.getNonPrefixArgument().get());
        assertEquals("98765432", result.getNonRepeatableArgumentValue(Parser.phoneNumberArg).get());
        assertEquals("johnd@gmail.com", result.getNonRepeatableArgumentValue(Parser.emailArg).get());
        assertEquals("John street, block 123, #01-01", result.getNonRepeatableArgumentValue(Parser.addressArg).get());
    }
}
