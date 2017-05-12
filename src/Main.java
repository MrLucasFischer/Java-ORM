//import bookstore.Author;
//import bookstore.Book;

import bookstore.Book;
import metamodels.Attribute;
import metamodels.Class;
import metamodels.Model;
import metamodels.Relation;
import sun.net.www.protocol.http.AuthenticationHeader;
import utils.sqlite.SQLiteConn;
import utils.transformations.Model2Model;
import utils.transformations.Model2Text;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        startProgram();
        //testORM();
    }

    public static String getModelChoice() {
        System.out.println("Which model do you want ? (Answer with the number of the desired model)");
        System.out.println("1 - BookStore");
        System.out.println("2 - Person");
        System.out.println("3 - Person(XMI model)");

        String modelChoice = sc.nextLine();
        while (!modelChoice.equals("1") && !modelChoice.equals("2") && !modelChoice.equals("3")) {
            System.out.println("Invalid Choice");
            modelChoice = sc.nextLine();
        }
        return modelChoice;
    }

    public static String getLanguageChoice() {
        System.out.println("Do you want it in Java or XML ? (Answer with the desired number)");
        System.out.println("1 - Java");
        System.out.println("2 - XML");
        String languageChoice = sc.nextLine();
        while (!languageChoice.equals("1") && !languageChoice.equals("2")) {
            System.out.println("Invalid Choice");
            languageChoice = sc.nextLine();
        }
        return languageChoice;
    }

    public static void startProgram() {

        String modelChoice = getModelChoice();
        String languageChoice = "";
        if (modelChoice.equals("3")) {
            Model model = Model2Model.getModel("src/models/person.xmi", false);
            buildModel(model);
        } else {
            languageChoice = getLanguageChoice();
        }


        if (modelChoice.equals("1") && languageChoice.equals("1")) {
            buildModel(getBookStoreModel());
        }
        if (modelChoice.equals("2") && languageChoice.equals("1")) {
            buildModel(getPersonModel());
        }
        if (modelChoice.equals("1") && languageChoice.equals("2")) {
            Model model = Model2Model.getModel("src/models/bookstore.xml", true);
            buildModel(model);
        }
        if (modelChoice.equals("2") && languageChoice.equals("2")) {
            Model model = Model2Model.getModel("src/models/person.xml", true);
            buildModel(model);
        }
    }

    public static Model getPersonModel() {
        Model model = new Model("Person");

        Class person = new Class("Person");
        person.addAttribute(new Attribute("name", "String"));
        person.addAttribute(new Attribute("age", "int"));

        model.addClass(person);
        return model;
    }

    public static Model getBookStoreModel() {
        Model model = new Model("BookStore");

        Class author = new Class("Author");
        author.addAttribute(new Attribute("first_name", "String"));
        author.addAttribute(new Attribute("last_name", "String"));
        author.addAttribute(new Attribute("email", "String"));
        author.setPkg(model.getName().toLowerCase());

        Class book = new Class("Book");
        book.addAttribute(new Attribute("title", "String"));
        book.addAttribute(new Attribute("pubDate", "String"));
        book.addAttribute(new Attribute("price", "double"));
        book.addAttribute(new Attribute("quantity", "int"));
        book.setPkg(model.getName().toLowerCase());

        Relation relation = new Relation(book, author, "12N", false, true);
        book.addRelation(relation);
        author.addRelation(relation);

        model.addClass(author);
        model.addClass(book);
        return model;
    }

    public static void buildModel(Model model) {

        // Generate SQL tables
        Model2Text model2Text = new Model2Text("src/templates");
        String sqlTables = model2Text.render(model, "sqlite3_create.ftl");
        System.out.println(sqlTables);
        //File f = new File("src/" + model.getName().toLowerCase());
        //f.mkdirs();

        SQLiteConn sqLiteConn = new SQLiteConn("src/" + model.getName().toLowerCase() + "/" + model.getName().toLowerCase() + ".db");
        //sqLiteConn.execute(sqlTables);

        // Generate Java classes
        for (Class c : model.getClasses()) {
            String javaClasses = model2Text.render(c, "java_class.ftl");
            System.out.println(javaClasses);
            try {
                File fout = new File("src/" + model.getName().toLowerCase() + "/" + c.getName() + ".java");
                FileOutputStream fos = new FileOutputStream(fout);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                bw.write(javaClasses);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void testORM() {
    }
}