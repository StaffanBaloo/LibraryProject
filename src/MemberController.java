import java.util.ArrayList;
import java.util.Scanner;


public class MemberController {
    MemberService memberService = new MemberService();
    Scanner scanner = new Scanner(System.in);

    public void showMemberMenu(){
        boolean active = true;

        while (active) {
            System.out.println("======Member Meny=====");
            System.out.println("1. Show all members");
            System.out.println("0. Back");
            int choice=Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:{
                    ArrayList<Member> members = memberService.getAllMembers();
                    for (Member member : members) {
                        System.out.println(member.toString());
                    }
                    break;
                }
                case 0: {
                    active = false;
                    break;
                }
            }
        }
    }
}
