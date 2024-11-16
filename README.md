# ATM-Simulation Project

<h3>Overview</h3>
The ATM-Simulation project is a Java-based application designed to simulate ATM operations, including managing customer accounts, processing deposits, withdrawals, and querying account balances. This application also incorporates a database backend for storing customer and transaction data, and email functionality for sending notifications to users.

### Key Features:
- **Customer Account Management**: Store and update customer information such as ID, name, phone number, and balance.
- **Transaction Handling**: Process deposit and withdrawal transactions with updates to the customer’s balance.
- **Database Integration**: SQLite database is used to store customer and transaction data.
- **Email Notifications**: Integration with an email system to notify customers about transaction updates.

### Features
- **Customer Table**: Contains fields for customer ID, name, phone number, and balance.
- **Storage Table**: Tracks denominations of paper bills available in the ATM (e.g., five, ten, twenty, fifty, hundred, twohundred).
- **Transaction Logic**: Handles deposit and withdrawal logic, updating customer balances and the storage table accordingly.
- **Email Notifications**: Sends email notifications for specific transactions using the `Mail` class.

### Technologies
- **Java**: Core programming language for the project.
- **JUnit**: Unit testing framework to ensure the correctness of the program logic.
- **PowerMock**: Used for mocking static methods and constructors for testing email functionality.
- **SQLite**: Lightweight database for storing customer and transaction data.
- **JavaMail**: Library for sending email notifications.

### Database

The project uses an SQLite database to store customer and transaction data. The schema includes two tables:

1. **Customers Table**
    - `id` (INTEGER): Customer's unique identifier.
    - `name` (TEXT): Customer's full name.
    - `mail` (TEXT): Customer's email address.
    - `phone` (TEXT): Customer's phone number.
    - `money` (DOUBLE): Customer's current balance.

2. **Storage Table**
    - `five`, `ten`, `twenty`, `fifty`, `hundred`, `twohundred` (INTEGER): Number of bills available for each denomination in the ATM.


### Usage

1. **Clone the Repository**:

    ```bash
    git clone https://github.com/your-username/ATM-Simulation.git
    cd ATM-Simulation
    ```

2. **Setup Database**:
   Ensure that you have SQLite installed. The database files will be located under `src/main/resources/` with the names:
   - `storage.db` (for ATM storage)
   - `customers.db` (for customer accounts)

3. **Run the Application**:
   You can run the application by compiling the Java classes and executing the main class that simulates the ATM operations.

   ```bash
   run
   ```
