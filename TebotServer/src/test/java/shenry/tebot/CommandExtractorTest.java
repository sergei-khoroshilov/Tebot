package shenry.tebot;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class CommandExtractorTest {

    // region getCommand

    private Object[] getCommand_TestValues() {
        return new Object[] {
                new Object[] {"", ""},
                new Object[] {null, ""},
                new Object[] {"command", "command"},
                new Object[] {" command", "command"},
                new Object[] {"command ", "command"},
                new Object[] {"/command", "/command"},
                new Object[] {"/command some values", "/command"},
                new Object[] {"/command\tsome values", "/command"},
                new Object[] {"/command, some values", "/command,"},
        };
    }

    @Test
    @Parameters(method = "getCommand_TestValues")
    public void getCommand_Success(String message, String expectedCommand) {
        CommandExtractor commandExtractor = getCommandExtractor();

        String actualCommand = commandExtractor.getCommand(message);

        Assert.assertEquals(expectedCommand, actualCommand);
    }

    // endregion

    // region removeCommand

    private Object[] removeCommand_TestValues() {
        return new Object[] {
                new Object[] {"message", "", "message"},
                new Object[] {"message", null, "message"},
                new Object[] {"message without command", "", "message without command"},
                new Object[] {"command message", "command", "message"},
                new Object[] {" command message ", "command", "message"},
        };
    }

    @Test
    @Parameters(method = "removeCommand_TestValues")
    public void removeCommand_Success(String message, String command, String expectedMessage) {
        CommandExtractor commandExtractor = getCommandExtractor();

        String actualMessage = commandExtractor.removeCommand(message, command);

        Assert.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void removeCommand_NullMessage_Exception() {
        CommandExtractor commandExtractor = getCommandExtractor();
        boolean thrown = false;

        try {
            commandExtractor.removeCommand(null, "command");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }

        Assert.assertEquals(true, thrown);
    }

    @Test
    public void removeCommand_MessageWithoutCommand() {
        CommandExtractor commandExtractor = getCommandExtractor();
        boolean thrown = false;

        try {
            commandExtractor.removeCommand("message without command", "command");
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }

        Assert.assertEquals(true, thrown);
    }

    // endregion

    private CommandExtractor getCommandExtractor() {
        return new CommandExtractor();
    }
}
