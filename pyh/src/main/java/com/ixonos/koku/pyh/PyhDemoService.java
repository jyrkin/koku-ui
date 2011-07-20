package com.ixonos.koku.pyh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ixonos.koku.pyh.mock.User;
import com.ixonos.koku.pyh.model.Dependant;
import com.ixonos.koku.pyh.model.Family;
import com.ixonos.koku.pyh.model.FamilyMember;
import com.ixonos.koku.pyh.model.Guardian;
import com.ixonos.koku.pyh.model.Guardianship;
import com.ixonos.koku.pyh.model.Person;

@Service (value = "pyhDemoService")
public class PyhDemoService {
  
  private static Logger log = LoggerFactory.getLogger(PyhDemoService.class);
  
  private PyhDemoModel model;
  private User user;
  private List<Person> searchedUsers;
  
  public PyhDemoService() {
    if (model == null) {
      this.model = PyhDemoFactory.createModel();
    }
    if (user == null) {
      this.user = new User("010101-1010", "guardian");
      //this.user = new User("030405-3456", "guardian");
    }
    
  }
  
  public Person getUser() {
    List<Person> persons = model.getPersons();
    Iterator<Person> pi = persons.iterator();
    while (pi.hasNext()) {
      Person person = pi.next();
      if (person.getSsn().equals(user.getSsn())) {
        return person;
      }
    }
    return null; // unrecoverable error; user not found
  }
  
  public List<Dependant> getDependants() {
    List<Dependant> dependants = new ArrayList<Dependant>(); 
    
    List<Guardianship> guardianships = model.getGuardianships();
    Iterator<Guardianship> gsi = guardianships.iterator();
    while (gsi.hasNext()) {
      Guardianship gs = gsi.next();
      
      List<Guardian> guardians = gs.getGuardians();
      Iterator<Guardian> gi = guardians.iterator();
      while (gi.hasNext()) {
        Guardian g = gi.next();
        if (g.getSsn().equals(user.getSsn())) {
          
          // if user SSN matches guardian's SSN then we have correct guardianship 
          // and we can return guardianship's dependants (that is user's dependants)
          dependants = gs.getDependants();
        }
      }
    }
    
    //log.info("calling PyhDemoService.getDependants: returning dependants.size = " + dependants.size());
    return dependants;
  }
  
  public List<FamilyMember> getOtherFamilyMembers() {
    List<FamilyMember> otherFamilyMembers = new ArrayList<FamilyMember>();
    
    //log.info("calling PyhDemoService.getOtherFamilyMembers");
    //log.info("userSSN: " + user.getSsn());
    
    Family usersFamily = getUsersFamily();
    
    List<FamilyMember> fmList = usersFamily.getFamilyMembers();
    
    Iterator<FamilyMember> fmi = fmList.iterator();
    while (fmi.hasNext()) {
      FamilyMember fm = fmi.next();
      if (fm.getSsn().equals(user.getSsn())== false && isUsersDependant(fm.getSsn()) == false) {
        otherFamilyMembers.add(fm);
      }
    }
    
    //log.info("PyhDemoService.getOtherFamilyMembers: otherFamilyMembers.size() = " + otherFamilyMembers.size());
    
    return otherFamilyMembers;
  }
  
  public void searchUsers(String firstname, String surname, String ssn) {
    log.info("calling PyhDemoService.searchUsers: SEARCHING USERS");
    
    clearSearchedUsers();
    
    if (firstname.equals("") == false || surname.equals("") == false || ssn.equals("") == false) {
      Iterator<Person> pi = model.getPersons().iterator();
      while (pi.hasNext()) {
        Person person = pi.next();
        if (person.getFirstname().equalsIgnoreCase(firstname) || person.getSurname().equalsIgnoreCase(surname) || 
            person.getSsn().equalsIgnoreCase(ssn)) {
          searchedUsers.add(person);
        }
      }
    }
  }
  
  public List<Person> getSearchedUsers() {
    if (searchedUsers == null) {
      log.info("returning searchedUsers: null");
    } else {
      log.info("returning searchedUsers: NOT null, size: " + searchedUsers.size());
    }
    return searchedUsers;
  }
  
  public void clearSearchedUsers() {
    if (searchedUsers == null) {
      searchedUsers = new ArrayList<Person>();
    }
    else {
      searchedUsers.clear();
    }
  }
  
  public void addDependantAsFamilyMember(String dependantSSN) {
    Dependant dependant = model.getDependant(dependantSSN);
    dependant.setMemberOfUserFamily(true);
    Family family = getUsersFamily();
    family.addFamilyMember(new FamilyMember(dependant, "lapsi"));
    
    // testing
//    log.info("new family member added, members now are:");
//    List<FamilyMember> members = family.getFamilyMembers();
//    Iterator<FamilyMember> fmi = members.iterator();
//    while (fmi.hasNext()) {
//      FamilyMember fm = fmi.next();
//      log.info(fm.getFirstname() + " " + fm.getSurname());
//    }
  }
  
  public void addNewDependant(Person dependant) {
    model.addPerson(dependant);
    
    // TODO: add dependant as user's dependant
    
    // get user's guardianship or create if does not exist
    Guardianship guardianship = model.getGuardianship(user.getSsn());
    if (guardianship == null) {
      ArrayList<Dependant> dependants = new ArrayList<Dependant>();
      ArrayList<Guardian> guardians = new ArrayList<Guardian>();
      guardianship = new Guardianship(dependants, guardians);
      
      Person person = model.getPerson(user.getSsn());
      // TODO: how to get guardian's role?
      guardianship.addGuardian(new Guardian(person, "rooli"));
    }
    guardianship.addDependant(new Dependant(dependant));
  }
  
  public void removeFamilyMember(String familyMemberSSN) {
    Family family = getUsersFamily();
    FamilyMember familyMember = family.getFamilyMember(familyMemberSSN);
    family.removeFamilyMember(familyMember);
  }
  
  public void addPersonsAsFamilyMembers(HashMap<String, String> personMap) {
    
    // TODO: add persons in map as family members
    
//    Person person = model.getPerson(personSSN);
//    Family family = getUsersFamily();
//    family.addFamilyMember(new FamilyMember(person, role));
  }
  
  private Family getUsersFamily() {
    Iterator<Family> fi = model.getFamilies().iterator();
    while (fi.hasNext()) {
      Family f = fi.next();
      if (f.isFamilyMember(user.getSsn())) {
        return f;
      }
      log.info("-- next family --");
    }
    return null;
  }
  
  private boolean isUsersDependant(String dependantSSN) {
    String userSSN = user.getSsn();
    List<Guardianship> guardianships = model.getGuardianships();
    Iterator<Guardianship> gi = guardianships.iterator();
    while (gi.hasNext()) {
      Guardianship g = gi.next();
      if (g.guardianshipExists(user.getSsn(), dependantSSN)) {
        return true;
      }
    }
    return false;
  }
  
}
