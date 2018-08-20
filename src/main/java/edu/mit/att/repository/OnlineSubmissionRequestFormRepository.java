package edu.mit.att.repository;

import edu.mit.att.entity.OnlineSubmissionRequestForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OnlineSubmissionRequestFormRepository extends JpaRepository<OnlineSubmissionRequestForm, Integer> {
    public List<OnlineSubmissionRequestForm> findByDepartmentAndAddressAndNameAndEmailAndPhoneAndDepartmentheadAndSignature(String department, String address, String name, String email, String phone, String departmenthead, String signature);

    public OnlineSubmissionRequestForm findById(int id);

    public List<OnlineSubmissionRequestForm> findBySsaid(int ssaid);
}
