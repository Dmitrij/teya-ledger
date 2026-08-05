# TEYA LEDGER

### Step #0 - initial workspace setup
Project name, Git repo, README (where I'm also going to track time spent)

// 5 min spent

### Step #1 - Interview the Client and functional analysis
Understand functional requirements...

#### Known functional requirements:
* Ability to record money movements (ie: deposits and withdrawals)
* View current balance
* View transaction history 

#### Clarifying questions: 
// This part is based only on my assumptions in order to keep system simple
* Q: Multi users (keep ony single tenant vs multi account) - A: Please try to keep it simple - one global account
* Q: Multi Currency - A: omitted. 
* Q: Dealing with overdrafts (i.e. going to negative balance) - A: NO, block
* Q: Deposits/Withdrawals (Transactions) validation (i.e. min/max) - A: let's stick to +- 1000.00 min/max per transaction
* Q: Deposits/Withdrawals (Transactions) reference/description - A: NO
* Q: Precision - A: use industry standards
* Q: Current Balance storage (dynamic on the fly calculation by transaction history vs aggregated state) - A: use industry standard
* *** Q: Current Balance (like in banks) available (reserved transactions) vs actual - A: keep simple
* Q: Transaction filtering (pagination, deposit/withdrawls, dates, etc...) - A: omitted, return all records
* Q: Transaction ordering (by date, by ??? ) - A: omitted, return as is from BD

#### Non functional boundaries (TBD)
* P: Using in-memory BD or Structure we are going to lose all data up on restart. - S: let's add "data initializer" 
* P: Let's add H2 DB support to demonstrate "transactions", not only "synchronized blocks"

// 20 min spent 



