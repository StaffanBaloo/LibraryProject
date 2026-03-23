import java.util.ArrayList;

public class MemberService {

    MemberRepository memberRepository = new MemberRepository();


    public ArrayList<Member> getAllMembers(){
            return memberRepository.getAllMembers();
        }
}
