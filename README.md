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
 
    Candidate 1 | tx=3 | attempts= 5000 | 7 ms
    Candidate 2 | tx=3 | attempts= 5000 | 6 ms
    Candidate 3 | tx=3 | attempts= 1269 | 1 ms | Winner
    Candidate 4 | tx=3 | attempts= 0 | 0 ms
    Candidate 5 | tx=3 | attempts= 0 | 0 ms
    Block 98 | tx=3 | prev=000315c979b2cc941c9e35a7cbbcc47823fd26efb7d2dd0d11e6ca27a76be028 | root=c1020331098de2afa178cc79ec9cee4510bbae09070a9a7b3996a9ae18b41638 | hash=000edb9e700f0b99034cae289c01172310eaf0d43a23352a1a529e3f43be16e2 | diff=3 | nonce=1268
 
    Users: 1000, generated tx: 10000
    Blocks: 98, included: 9661, rejected: 0, left: 339
    Initial total: 496285277 -> 496285277
    Time: 1323 ms
 
Remaining 339 tx: senders lack balance.
 
## Use of AI
 
Claude (Anthropic): structure, explanations.