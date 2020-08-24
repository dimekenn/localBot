package kz.qbots.command;


import kz.qbots.command.impl.*;
import kz.qbots.exeption.NotRealizedMethodException;

public class CommandFactory {

    public static Command getCommand(long id) {
        Command result = getCommandWithoutReflection((int) id);
        if (result == null) throw new NotRealizedMethodException("Not realized for type: " + id);
        return result;
    }
    private static Command getCommandWithoutReflection(int id) {
        switch (id) {
            case 1:
                return new id001_ShowInfo();
            case 2:
                return new id002_SelectLang();
            case 3:
                return new id003_ClientShowInfo();
            case 20:
                return new id020_Menu();
            case 21:
                return new id021_Request();
            case 22:
                return new id022_AboutUs();
            case 23:
                return new id023_Services();
            case 24:
                return new id024_Contacts();

        }

        return null;
    }
}
