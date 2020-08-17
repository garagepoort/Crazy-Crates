package me.badbones69.crazycrates.commands;

public class CommandInputValidator {

    public static void validate(CommandInputValidated function, String exceptionMessage) {
        if(!function.isValid()) {
            throw new CommandInputException(exceptionMessage);
        }
    }
}
