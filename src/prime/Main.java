package prime;

import member.Member;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static Member loggedInUser;

    public static void main(String[] args) {
        MainController mainController = new MainController();
        mainController.showMainMenu();
    }

    public static void login(Member member) {
        loggedInUser = member;
    }

    public static void logout(){
        loggedInUser=null;
    }
}