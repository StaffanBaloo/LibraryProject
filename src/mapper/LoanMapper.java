package mapper;

import loan.LoanListDTO;
import loan.Loan;

public class LoanMapper {

    public static LoanListDTO maptoDTO(Loan loan){
        return new LoanListDTO(loan.getId(), BookMapper.maptoLoanDTO(loan.getBook()), loan.getMember(), loan.getLoanDate(), loan.getDueDate(), loan.getReturnDate());
    }
}
