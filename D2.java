import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class D2 {
    protected Connection con;
    protected ArrayList<String> arrayData;
    protected ASCIIArtGenerator artGen;
    protected Boolean Customer=false;
    protected Boolean Employee=false;
    protected int R;
    public static void main(String args[]) throws Exception {
        D2 session = new D2();
        session.R=771;
        try{
        //load the driver class  
        Class.forName("oracle.jdbc.driver.OracleDriver");  
        //create  the connection object  
        session.con=DriverManager.getConnection(  
        "jdbc:oracle:thin:@localhost:1521:xe","SYS as SYSDBA","admin123"); 
        
        }
        catch(Exception e){ System.out.println(e);}
        session.artGen = new ASCIIArtGenerator();
        System.out.println();
        session.artGen.printTextArt("Welcome to CSI Hotels!", ASCIIArtGenerator.ART_SIZE_SMALL, ASCIIArtFont.ART_FONT_MONO,"|");
        System.out.println();
       session.enterUser();
    }
    private void enterUser() throws Exception{
        System.out.print("Enter Username : ");
        Scanner info = new Scanner(System.in);
        String userName = info.nextLine();
        System.out.print("Enter Password : ");
        String password = info.nextLine();
        typeOfUser(userName, password);
        info.close();
    }


    //checks if the username corresponds to a customer or an employee
    private void typeOfUser(String userName, String password) throws Exception{
        if(userName=="admin" && password=="admin123"){
            admin();
        }
        databaseAcces("SELECT FullName FROM Employee");
        for (int i = 0; i < arrayData.size(); i++) {
            if(userName.equals(arrayData.get(i))){
                employeeInfo(userName);
            }
            ;
          }
          databaseAcces("SELECT FullName FROM Customer");
        for (int i = 0; i < arrayData.size(); i++) {
            if(userName.equals(arrayData.get(i))){
                customerInfo(userName);
            };
          }
    }
    private void customerInfo(String userName) throws Exception{
        Customer = true;
        System.out.println("");
        System.out.println("welcome customer "+userName);
        selectHotelBrand();
    }
    private void employeeInfo(String userName) throws Exception{
        Employee = true;
        System.out.println("");
        System.out.println("welcome employee "+userName);
        command();
    }
    private void command() throws Exception{
        Scanner ss = new Scanner(System.in);
        String cmdLine = ss.nextLine();
        switch(cmdLine) {
            case "-h":
            System.out.println("-h for help");
            System.out.println("-Rent to transform booked room into rented rooms when the customers arrives or find a room and rent it without prior booking");
            System.out.println("y for yes");
            System.out.println("n for no");
            System.out.println("-lookup to lookup available rooms and the ones that are booked");
            System.out.println("-delete to drop a booking in the absence of the customer");
              break;
            case "-Rent":
            System.out.println("Does the customer already have a booking? (y/n)");
            cmdLine = ss.nextLine();
                switch(cmdLine) {
                    case "y":
                    arrivedWithBooking();
                    break;
                    case "n":
                    arrivedWithNOBooking();
                    break;
                    default:
                    System.out.println("WRONG COMMAND!");
                }
              break;
            case "-lookup":
              lookUp();
              break;
            case "-delete":
              notArrived();
              break;
            default:
            System.out.println("WRONG COMMAND!");
            System.out.println("-h for help");
          }
          ss.close();
    }

    private void selectHotelBrand() throws Exception{
        System.out.println();
        artGen.printTextArt("Hotel Brands :", ASCIIArtGenerator.ART_SIZE_SMALL, ASCIIArtFont.ART_FONT_MONO,"|");
        System.out.println();
        try{
        databaseAcces("SELECT * FROM HotelBrand");
        for (int i = 0; i < arrayData.size(); i++) {
            System.out.println(arrayData.get(i));
          }
        System.out.println("Choose a Hotel Brand!");
        Scanner scn = new Scanner(System.in);
        String brandChosen = scn.nextLine();;
        
            databaseAcces("SELECT BName FROM HotelBrand");
            boolean match = false;
            while(match==false){
                
                
                for (int i = 0; i < arrayData.size(); i++) {
                    if(brandChosen.equals(arrayData.get(i))){
                    match = true;
                    break;
                    };
                }
                if(match==true){
                    break;
                }
                System.out.println("THE NAME OF THE BRAND IS INCORRECT, PLZ TRY AGAIN!");
                System.out.println("Hotel Brand : ");
                brandChosen = scn.nextLine();
            }
            selectHotel(brandChosen);
            scn.close();
        }catch(Exception e){ System.out.println(e);}
    }

    private void selectHotel(String brandChosen) throws Exception{
        System.out.println();
        artGen.printTextArt(brandChosen+"Hotels :", ASCIIArtGenerator.ART_SIZE_SMALL, ASCIIArtFont.ART_FONT_MONO,"*");
        System.out.println();
        try{
        databaseAcces("SELECT Hotel.HotelID,Hotel.Hname,Hotel.StarCatagory,Hotel.Address,Hotel.Email,Hotel.PhoneNumber FROM Owns JOIN Hotel ON Hotel.HotelID = Owns.HotelID JOIN HotelBrand ON HotelBrand.BrandID = Owns.BrandID where HotelBrand.BName = "+"'"+brandChosen+"'");
    
        for (int i = 0; i < arrayData.size(); i++) {
            System.out.println(arrayData.get(i));
          }
        System.out.println("Choose a Hotel (both name and ID) to stay in!");
        Scanner scn = new Scanner(System.in);
        String hotelChosen = scn.nextLine();
        String hotelID = scn.nextLine();
        
        databaseAcces("SELECT Hotel.Hname FROM Owns JOIN Hotel ON Hotel.HotelID = Owns.HotelID JOIN HotelBrand ON HotelBrand.BrandID = Owns.BrandID where HotelBrand.BName = "+"'"+brandChosen+"'");
            boolean match = false;
            while(match==false){
                
                
                for (int i = 0; i < arrayData.size(); i++) {
                    if(hotelChosen.equals(arrayData.get(i))){
                    match = true;
                    break;
                    };
                }
                if(match==true){
                    break;
                }
                System.out.println("THE NAME OF THE HOTEL IS INCORRECT, PLZ TRY AGAIN!");
                System.out.println("Hotel Name : ");
                hotelChosen = scn.nextLine();
                hotelID = scn.nextLine();
            }
            selectRoom(hotelChosen,hotelID);
            scn.close();
        }catch(Exception e){ System.out.println(e);}
        
    }

    private void selectRoom(String hotelChosen,String hotelID) throws Exception{
        System.out.println();
        artGen.printTextArt("Rooms available :", ASCIIArtGenerator.ART_SIZE_SMALL, ASCIIArtFont.ART_FONT_MONO,"*");
        System.out.println();
        try{
        databaseAcces("SELECT Room.RoomID,Price,RoomCapacity,View_Room,Extended_Room FROM Has JOIN Hotel ON Hotel.HotelID = Has.HotelID JOIN Room ON Room.RoomID = Has.RoomID where Hotel.Hname = '"+hotelChosen+"' and InUse=0");
        
        for (int i = 0; i < arrayData.size(); i++) {
            if(arrayData.get(i).equals("1")){
                arrayData.set(i, "This Room can be extended!");
            }
            else if(arrayData.get(i).equals("0")){
                arrayData.set(i, "This Room cannot be extended!");
            }
            System.out.println(arrayData.get(i));
          }
        System.out.println("Choose a Room (number) to Book!");
        Scanner scn = new Scanner(System.in);
        String roomChosen = scn.nextLine();;
        
        databaseAcces("SELECT Room.RoomID FROM Has JOIN Hotel ON Hotel.HotelID = Has.HotelID JOIN Room ON Room.RoomID = Has.RoomID where Hotel.Hname = '"+hotelChosen+"' and InUse=0");
            boolean match = false;
            while(match==false){
                
                
                for (int i = 0; i < arrayData.size(); i++) {
                    if(roomChosen.equals(arrayData.get(i))){
                    match = true;
                    break;
                    };
                }
                if(match==true){
                    break;
                }
                System.out.println("THE ROOM NUMBER IS INCORRECT, PLZ TRY AGAIN!");
                System.out.println("Room number : ");
                roomChosen = scn.nextLine();
            }
            bookRoom(roomChosen,hotelID);
            scn.close();
        }catch(Exception e){ System.out.println(e);}
    }

    private void bookRoom(String roomChosen,String hotelID) throws Exception{
        System.out.println();
        artGen.printTextArt("Bookings Available :", ASCIIArtGenerator.ART_SIZE_SMALL, ASCIIArtFont.ART_FONT_MONO,"*");
        System.out.println();
        try{
            databaseAcces("SELECT BookID,StartDate,EndDate, BookingConfiramtion FROM Booking JOIN Room ON Room.RoomID = Booking.RoomID where Room.RoomID = "+Integer.parseInt(roomChosen));
            
            for (int i = 0; i < arrayData.size(); i++) {
                
                System.out.println(arrayData.get(i));
              }
            System.out.println("Choose a Booking!");
            Scanner scn = new Scanner(System.in);
            String bookingChosen = scn.nextLine();;
            
            databaseAcces("SELECT BookID FROM Booking JOIN Room ON Room.RoomID = Booking.RoomID where Room.Room.ID = "+roomChosen);
                boolean match = false;
                while(match==false){
                    
                    
                    for (int i = 0; i < arrayData.size(); i++) {
                        if(bookingChosen.equals(arrayData.get(i))){
                        match = true;
                        break;
                        };
                    }
                    if(match==true){
                        break;
                    }
                    System.out.println("THE BOOKING NUMBER IS INCORRECT, PLZ TRY AGAIN!");
                    System.out.println("Booking number : ");
                    bookingChosen = scn.nextLine();
                }
                confirmBooking(bookingChosen,roomChosen,hotelID);
                scn.close();
            }catch(Exception e){ System.out.println(e);}
    }

    private void confirmBooking(String bookingChosen,String roomChosen,String hotelID) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("plz enter following informations :");
        System.out.println("CutID");
        String CutID =sc.nextLine();
        System.out.println("FullName");
        String FullName =sc.nextLine();
        System.out.println("Address");
        String Address =sc.nextLine();
        System.out.println("SINnum");
        String SINnum =sc.nextLine();
        System.out.println("RegistrationDate");
        String RegistrationDate =sc.nextLine();
        System.out.println("StartDate");
        String StartDate =sc.nextLine();
        System.out.println("EndDate");
        String EndDate =sc.nextLine();
        databaseAcces("insert into Customer values("+CutID+","+FullName+","+Address+","+SINnum+","+RegistrationDate+","+StartDate+","+EndDate+")");
        databaseAcces("insert into Booking values("+bookingChosen+","+roomChosen+","+StartDate+","+EndDate+","+java.time.LocalDate.now()+")");
        databaseAcces("insert into Books valueas("+CutID+","+bookingChosen+")");
        databaseAcces("insert into ListOf"+hotelID+","+bookingChosen+","+roomChosen+")");
        sc.close();
    }
    private void lookUp() throws Exception{
        if(Employee==true){
            System.out.println("THE AVAILABLE ROOMS ARE THE FOLLOWING :");
            databaseAcces("SELECT Hname, RoomID FROM Has JOIN Hotel ON Hotel.HotelID = Has.HotelID JOIN Room ON Room.RoomID = Has.RoomID where InUse=0");
            System.out.println("THE FOLLOWING ROOMS ARE BOOKED :");
            databaseAcces("Select * from ListOf JOIN Hotel ON Hotel.Hotelid=listof.hotelid");
        }else{
            System.out.println("ACCESS DENIED!");
        }
    }
    private void notArrived() throws Exception{
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the BookingID");
        String BookID =sc.nextLine();
        sc.close();
        databaseAcces("delete from Booking where BookID = "+BookID);
        databaseAcces("delete from Books where BookID = "+BookID);
        databaseAcces("delete from ListOf where BookID = "+BookID);
    }
    private void arrivedWithBooking() throws Exception{
        System.out.println("Insert The necessary informations!");
        Scanner sc = new Scanner(System.in);
        System.out.println("CutID");
        String CutID =sc.nextLine();
        System.out.println("BookID");
        String BookID =sc.nextLine();
        System.out.println("RoomID");
        String RoomID =sc.nextLine();
        System.out.println("EmpID");
        String EmpID =sc.nextLine();
        System.out.println("StartDate");
        String StartDate =sc.nextLine();
        System.out.println("EndDate");
        String EndDate =sc.nextLine();
        databaseAcces("insert into Transforsto values("+BookID+","+RoomID+")");
        arrivedWithBooking(RoomID, CutID, EmpID, StartDate,EndDate);
        sc.close();
    }
    private void arrivedWithBooking(String RoomID,String CutID,String EmpID,String StartDate,String EndDate) throws Exception{
        R++;
        
        databaseAcces("insert into Rents values("+RoomID+","+CutID+")");
        databaseAcces("insert into InChargeOf values("+String.valueOf(R)+","+EmpID+")");
        databaseAcces("insert into Renting values("+String.valueOf(R)+","+RoomID+","+StartDate+","+EndDate+")");
    }
    private void arrivedWithNOBooking() throws Exception{
        Scanner sc = new Scanner(System.in);
        System.out.println("Insert The customer's informations!");
        System.out.println("CutID");
        String CutID =sc.nextLine();
        System.out.println("EmpID");
        String EmpID =sc.nextLine();
        System.out.println("StartDate");
        String StartDate =sc.nextLine();
        System.out.println("EndDate");
        String EndDate =sc.nextLine();
        sc.close();
        System.out.println("THE AVAILABLE ROOMS ARE THE FOLLOWING :");
        try{
        databaseAcces("SELECT Hname, RoomID FROM Has JOIN Hotel ON Hotel.HotelID = Has.HotelID JOIN Room ON Room.RoomID = Has.RoomID where InUse=0");
        for (int i = 0; i < arrayData.size(); i++) {
            if(arrayData.get(i).equals("1")){
                arrayData.set(i, "This Room can be extended!");
            }
            else if(arrayData.get(i).equals("0")){
                arrayData.set(i, "This Room cannot be extended!");
            }
            System.out.println(arrayData.get(i));
          }
        System.out.println("Choose a Room (number) to Book!");
        Scanner scn = new Scanner(System.in);
        String roomChosen = scn.nextLine();;
        
        databaseAcces("SELECT Room.RoomID FROM Has JOIN Hotel ON Hotel.HotelID = Has.HotelID JOIN Room ON Room.RoomID = Has.RoomID where InUse=0");
            boolean match = false;
            while(match==false){
                
                
                for (int i = 0; i < arrayData.size(); i++) {
                    if(roomChosen.equals(arrayData.get(i))){
                    match = true;
                    break;
                    };
                }
                if(match==true){
                    break;
                }
                System.out.println("THE ROOM NUMBER IS INCORRECT, PLZ TRY AGAIN!");
                System.out.println("Room number : ");
                roomChosen = scn.nextLine();
            }
            arrivedWithBooking(roomChosen, CutID, EmpID, StartDate,EndDate);
            scn.close();
            
        }catch(Exception e){ System.out.println(e);}
    }
    
    private void admin(){
        System.out.println("Welcome admin!...");
        System.out.println("You may now start editing the database...");
        System.out.println("Enter 'quit' to end task!");
        Scanner cv = new Scanner(System.in);
        String qString = cv.nextLine();
        while(!(qString.equals("quit"))){
        try{
            databaseAcces(qString);
        }catch(Exception e){ System.out.println(e);}
    }
    cv.close();
    }

    private void databaseAcces(String queuryString) throws Exception{
        arrayData = new ArrayList<String>();
        try{  
            Statement stmt=con.createStatement();
            //execute query  
            ResultSet rs=stmt.executeQuery(queuryString);
            
            ResultSetMetaData rsmd = rs.getMetaData();
int columnsNumber = rsmd.getColumnCount();
boolean stat=false;
System.out.println("");
while (rs.next()){
    
    if(stat==false){
for (int x = 1; x <= columnsNumber && stat==false; x++) {
    
   // if (x > 1 && x<=columnsNumber) System.out.print(" | ");
      //System.out.print(rsmd.getColumnName(x));
       
}
stat = true;
//System.out.println("");
//System.out.println("---------");
    }

}
ResultSet rs2=stmt.executeQuery(queuryString);
while (rs2.next()) {
    for (int i = 1; i <= columnsNumber; i++) {
        //if (i > 1) System.out.print(" | ");
        String columnValue = rs2.getString(i);
        arrayData.add(columnValue);
        //System.out.print(columnValue + " " );
    }
    arrayData.add("\n");
    //System.out.println("");
    
}
              
            }catch(Exception e){ System.out.println(e);
            con.close();}
    }
}