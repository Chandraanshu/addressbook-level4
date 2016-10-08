package seedu.address.logic.parser;

import org.junit.BeforeClass;
import seedu.address.logic.parser.ArgumentsParser.ParsedArguments;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArgumentsParserTest {
    /**
     * Makes sure that static members of Parser are initialized
     */
    @BeforeClass
    public static void initParser() {
        Parser parser = new Parser();
    }

    @Test
    public void parse_validAddCmdArgsNoTags() {
        ArgumentsParser argsParser = new ArgumentsParser(Parser.addCmdArgs);
        ParsedArguments result = argsParser.parse(
                "John Doe p/98765432 e/johnd@gmail.com a/John street, block 123, #01-01");

        assertEquals("John Doe", result.getNonPrefixArgument().get());
        assertEquals("98765432", result.getOnceArgumentValue(Parser.phoneNumberArg).get());
        assertEquals("johnd@gmail.com", result.getOnceArgumentValue(Parser.emailArg).get());
        assertEquals("John street, block 123, #01-01", result.getOnceArgumentValue(Parser.addressArg).get());
    }

    @Test
    public void parse_validAddCmdArgsWithTags() {
        ArgumentsParser argsParser = new ArgumentsParser(Parser.addCmdArgs);
        ArgumentsParser.ParsedArguments result = argsParser.parse(
                "Betsy Crowe p/1234567 e/betsycrowe@gmail.com a/Newgate Prison t/criminal t/friend");
        List<String> tags = new ArrayList<>();
        tags.add("criminal");
        tags.add("friend");

        assertEquals("Betsy Crowe", result.getNonPrefixArgument().get());
        assertEquals("1234567", result.getOnceArgumentValue(Parser.phoneNumberArg).get());
        assertEquals("betsycrowe@gmail.com", result.getOnceArgumentValue(Parser.emailArg).get());
        assertEquals("Newgate Prison", result.getOnceArgumentValue(Parser.addressArg).get());
        assertEquals(tags, result.getRepeatableArgumentValue(Parser.tagArgs).get());
    }
}