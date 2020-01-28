RUNNING THE ATM AND BANK SYSTEM

  1. Record the ip and desired port number of each "bank" in the banks.csv file.
  2. Make copies of the entire Bank System project and distribute to the machines which will be running the bank and atm programs.
  3. Optionally edit the accounts.csv file for each bank, to ensure each bank has different accounts loaded. Do not use excel, as values get rounded. make sure the accounts start with the first 2 digits that denotes the bankID of the bank
  4. Start the bank programs, and enter their IDs, they will connect to each other as they start up.
  5. Start the ATM programs, enter which bank each atm belongs to, and it will find that bank from the banks.csv file.
  6. Once atm display is up, follow the onscreen prompts to interact with accounts at any of the banks.

RUNNING THE DEMO
Locally:
Open 5 terminals. Navigate in each one to the corresponding folder.
On 4 of them type: javac Bank.java
On 1 type: javac ATM.java
Start each one by typing java ATM or java Bank

Enter the id of each bank (11,22,33,44);
On the atm terminal enter either id 11,22,33,44 (doesn't matter)

Try accessing an account from either bank. you can open accounts.csv in a text editor (dont use excel) and copy an account number and pin to try and access that account.

On many comps:
Follow local instructions but change the ips to the computer ips and make sure the OS does not block those ports.
