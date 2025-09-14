package api;

import api.dto.LoanResult;
import api.dto.QueryResult;
import api.dto.ReturnResult;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LibraryService extends Remote {
    LoanResult loanByIsbn(String isbn)   throws RemoteException;
    LoanResult   loanByTitle(String title) throws RemoteException;
    QueryResult queryByIsbn(String isbn)  throws RemoteException;
    ReturnResult returnByIsbn(String isbn) throws RemoteException;
}
