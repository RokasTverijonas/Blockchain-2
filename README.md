# Simplified Blockchain (v0.1)
 
## Run
 
    javac -d bin -sourcepath src src/Main.java
    java -cp bin Main
 
## Model
 
- **User:** id, name, unique `public_key`, balance 100 to 1 000 000.
- **Transaction:** `sender`, `receiver`, `amount`, `sequence`, `id`.
- **Block:** header `prev_hash`, `timestamp`, `version`, `tx_root`, `nonce`, `difficulty` + ordered transaction list.
- **Balance:** `public_key -> balance`
- **Genesis:** `prev_hash` = 64 zeros, empty transaction list.
## Hashing
 
- Hash function: v0.2 version from Assignment 1 (written with AI help), repo `RokasTverijonas/Blockchain-1`.
- Transaction ID = `H("sender|receiver|amount|sequence")`.
- Block hash = `H("prev_hash|timestamp|version|tx_root|nonce|difficulty")`.
- `tx_root` = `H(transaction IDs)`.
- Proof-of-Work: `difficulty = 3`, block hash must start with `000`.
## Transaction rules
 
- **Pool:** rejects with a stored reason: unknown participant, sender = receiver, non-positive amount, duplicate ID.
- **Candidate:** up to 100 random transactions (seeded shuffle), added only if the sender can afford them on a temporary copy of the balances. The rest stay in the pool.
- **Block acceptance:** `prev_hash`, `tx_root`, Proof-of-Work and transactions are re-validated.
## SEED
 
`283`
 
## Results
 
Block example:
 
    Block 97 | tx=54 | prev=000544cdabbf913f6727d6484006358bff8e396cbf7f2a13f1587501f068786e | root=ad93d14324b64e4e7b91b9ebd43948b0250bcf9db3e4d68a179421b7fa04cf0a | hash=00060edd6fa3f50959fbcecd1fad642b889005acaee06b98ce07737463505f54 | diff=3 | nonce=4497 | attempts=4498 | 5 ms
 
Final summary:
 
    Users: 1000, generated tx: 10000
    Blocks: 98, included: 9655, rejected: 0, left: 345
    Initial total: 496285277 -> 496285277
    Time: 861 ms