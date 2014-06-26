Hello fellas, **anDB** is a tool for working with sqlite from android devices
The idea is to use magic annotations to make your life as easy as possible

Here is a short sample

@Table(name="user")

public class User extends BaseTable{

  @Column(name="name", type="TEXT")

  private String name;

  @Column(name="email", type="CHAR(254)")

  private String email;

  @Column(name="password", type="CHAR(20)")

  private String password;

  @Column(name="field_int", type="INT")

  private int fieldInt;

  @Column(name="field_real", type="REAL")

  private int fieldReal;
}

## How to use

User user = new User();

user.setEmail("jhon@gmail.com");

user.setName("Bob");

user.setPassword("qwerty");

user.setFieldInt(12);

user.setFieldReal(13);


DBHandler dbHandler = new DBHandler(getApplicationContext());

dbHandler.dropTable(user); // just in case

dbHandler.createTable(User.class);

dbHandler.insertInto(user);

dbHandler.deleteRecord(users.get(0));

dbHandler.dropTable(user);`
