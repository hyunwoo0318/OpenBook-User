package Project.OpenBook.Domain.JJH.JJHListProgress;

import Project.OpenBook.Domain.Customer.Domain.Customer;

import java.util.List;

public interface JJHListProgressRepositoryCustom {

    public List<JJHListProgress> queryJJHListProgressWithJJHList(Customer customer);

    public List<JJHListProgress> queryAllJJHList();
}
