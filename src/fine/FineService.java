package fine;

import loan.Loan;
import member.Member;

import java.util.ArrayList;
import java.util.Optional;

public class FineService {
    FineRepository fineRepository = new FineRepository();

    public void createFine(Loan loan, int amount) {
        fineRepository.createFine(new Fine(loan, amount));
    }

    public void payFine (Fine fine) {
        fineRepository.payFine(fine);
    }

    public ArrayList<Fine> getAllFines(){
        return fineRepository.getAllFines();
    }

    public ArrayList<Fine> getAllUnpaidFinesForMember (Member member) {
        return fineRepository.getAllUnpaidFinesForMember(member);
    }

    public int getUnpaidFinesTotalByMemberId(Member member) {
        return fineRepository.getUnpaidFinesTotalByMemberId(member);
    }

    public boolean exists(int id) {
        return fineRepository.exists(id);
    }

    public Optional<Fine> getFineById(int id){
        return fineRepository.getFineById(id);
    }


}
