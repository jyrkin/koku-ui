package com.ixonos.koku.pyh;

import com.ixonos.koku.pyh.model.Child;
import com.ixonos.koku.pyh.model.Family;
import com.ixonos.koku.pyh.model.FamilyMember;
import com.ixonos.koku.pyh.model.Person;

public class PyhDemoFactory {
  
  public static PyhDemoModel createModel() {
    
    // create persons
    
    Person p1 = new Person("Matti", "Mikael", "Meik�l�inen", "010203-1234", "010203");
    Person p2 = new Person("Tytti", "Taina", "J�rvinen", "020304-2345", "020304");
    Person p3 = new Person("Jouni", "Josef", "Merinen", "030405-3456", "030405");
    Person p4 = new Person("Tapani", "Toivo", "Ruohonen", "040506-4567", "040506");
    Person p5 = new Person("Liisa", "Leila", "Ruohonen", "050607-5678", "050607");
    Person p6 = new Person("Pekka", "", "Peltola", "010101-1010", "010101");
    Person p7 = new Person("Piritta", "", "Peltola", "020202-2020", "020202");
    
    Child c1 = new Child("Tero", "Tapani", "Peltola", "111111-1111", "111111", "010101-1010");
    Child c2 = new Child("Tiina", "Terhi", "Peltola", "222222-2222", "222222", "010101-1010");
    
    Child c3 = new Child("Maija", "Mette", "Merinen", "333333-3333", "333333", "030405-3456");
    Child c4 = new Child("Janne", "Kari", "Merinen", "444444-4444", "444444", "030405-3456");
    Child c5 = new Child("Laura", "Liina", "Merinen", "555555-5555", "555555", "030405-3456");
    
    Child c6 = new Child("Heikki", "Juhani", "J�rvinen", "666666-6666", "666666", "020304-2345");
    
    PyhDemoModel model = new PyhDemoModel();
    model.addPerson(p1);
    model.addPerson(p2);
    model.addPerson(p3);
    model.addPerson(p4);
    model.addPerson(p5);
    model.addPerson(p6);
    model.addPerson(c1);
    model.addPerson(c2);
    model.addPerson(c3);
    model.addPerson(c4);
    model.addPerson(c5);
    model.addPerson(c6);
    
    // create families
    
    Family f1 = new Family();
    f1.addFamilyMember(new FamilyMember(p6, "isa"));
    f1.addFamilyMember(new FamilyMember(p7, "aiti"));
    f1.addFamilyMember(new FamilyMember(c1, "lapsi"));
    f1.addFamilyMember(new FamilyMember(c2, "lapsi"));
    
    Family f2 = new Family();
    f2.addFamilyMember(new FamilyMember(p2, "aiti"));
    f2.addFamilyMember(new FamilyMember(c6, "lapsi"));
    
    model.addFamily(f1);
    model.addFamily(f2);
    
    return model;
  }
  
  
  
}
