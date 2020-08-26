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
            case 4:
                return new id004_ReportBug();
            case 5:
                return new id005_GroupManager();
            case 6:
                return new id006_SendMessageToCustomer();
            case 7:
                return new id007_RegisterCustomer();
            case 8:
                return new id008_RegisterDeveloper();
            case 9:
                return new id009_RegisterManager();
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
