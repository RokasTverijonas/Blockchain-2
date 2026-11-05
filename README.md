# Simplified Blockchain (v0.2)
 
## Run
 
    javac -d bin -sourcepath src src/Main.java
    java -cp bin Main
 
## Tests
 
`lib/junit.jar` = JUnit console 6.1.3 (Maven Central).
 
    javac -d bin -cp "lib/*" -sourcepath src src/test/*.java
    java -jar lib/junit.jar execute -cp bin --scan-class-path
 
`ValidationTest`: valid payment, bad tx rejected, balance exceeded, duplicates, bad block. `MerkleTreeTest`: 1, 2, 3, 5 tx, changed tx, swapped order.
 
## Model
 
- **User:** id, name, `public_key`, balance.
- **Transaction:** sender, receiver, amount, sequence, id.
- **Block:** prev_hash, timestamp, version, tx_root, nonce, difficulty, transactions.
- **Genesis:** zero prev_hash, no transactions, not mined.
## Hashing
 
- Hash: v0.2 from Assignment 1 (AI-assisted), `RokasTverijonas/Blockchain-1`.
- Transaction ID = `H("sender|receiver|amount|sequence")`.
- Block hash = `H("prev_hash|timestamp|version|tx_root|nonce|difficulty")`.
- `tx_root` = Merkle root of transaction IDs (odd node paired with its copy, empty block = `H(empty bytes)`).
- PoW: hash starts with `000`.
## Rules
 
- **Pool:** rejects invalid tx with a reason.
- **Candidate:** up to 100 tx, checked on a balances copy.
- **Block:** fully re-validated, state changes only if valid.
## Five candidates
 
- Same `prev_hash` and state, mined one by one.
- Budget: 5 000 attempts, doubles each round, limit 1 000 000.
- Winner: first to find a nonce.
**Not P2P consensus:** one process, no network, no independent nodes.
 
## Seed
 
`283`
 
## Results
 
    Candidate 1 | tx=100 | attempts= 5000 | 5 ms
    Candidate 2 | tx=100 | attempts= 5000 | 5 ms
    Candidate 3 | tx=100 | attempts= 3588 | 3 ms | Winner
    Candidate 4 | tx=100 | attempts= 0 | 0 ms
    Candidate 5 | tx=100 | attempts= 0 | 0 ms
    Block 73 | tx=100 | prev=000b51223605842d23290caa201cb0550e7cb52337806ebd8fbaf9dccfca420f | root=0054e07e70730d146f014a603cfb19b6d8d2e7d8dee026eccff3383e7c6db469 | hash=000aed22fde19d65e9e81fccc21cfd6fea045c7887d6352a3d1e7801bff3aa71 | diff=3 | nonce=3587
 
    Users: 1000, generated tx: 10000
    Blocks: 98, included: 9661, rejected: 0, left: 339
    Initial total: 496285277 -> 496285277
    Time: 1323 ms
 
Remaining 339 tx: senders lack balance.
 
## Use of AI
 
Claude (Anthropic): structure, explanations.