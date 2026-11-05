# Simplified Blockchain (v0.3)
 
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

## v0.3: AI improvements
 
Improved on branch `v0.3`. Two directions: Merkle tree efficiency and mining loop efficiency.
 
### Changes
 
| Change | File | Result |
|---|---|---|
| Merkle: node hex is decoded straight into one combined byte array (was: 3 arrays, `substring` + `parseInt` per byte) | `MerkleTree.java` | faster |
| Mining: constant header parts built once, `calculateHash()` only appends the nonce | `Block.java` | no measurable gain |
 
Rejected: replacing `String.format` in the hash hex output with a lookup table. It is the biggest cost, but it means changing the hash function, which I kept unchanged so that results are not mixed with a hash change.
 
### How it was checked
 
- Same 8 tests pass on v0.2 and v0.3.
- `Benchmark` prints `root` and `hash` for fixed data (100 tx, nonce 12345). Both are identical before and after, so the output did not change:
  - `root: 5a0e07f8bb036730c8f9e494cbf7e0818e8c07378fa8465a3751051c8c21f20e`
  - `hash: d44764081db7504b5870bd321e2d11382236a9953eca7c0003b147dd1efa65aa`
### Benchmark
 
    javac -d bin -sourcepath src src/test/Benchmark.java
    java -cp bin test.Benchmark
 
Fixed input, 1 warm-up, 5 measured runs, results printed after the measurements. Merkle = the root of the same 100-leaf tree computed 2000 times. Mining = 200 000 fixed attempts of the inner loop (set nonce, hash, check prefix), not a full search.
 
| | v0.2 (ms) | v0.3 (ms) |
|---|---|---|
| Merkle | 273 252 265 251 272, avg **262** | 233 226 223 228 228, avg **227** |
| Mining | 235 202 202 202 201, avg **208** | 212 198 199 205 198, avg **202** |
 
### Conclusion

- **Merkle:** about 13% faster (262 to 227 ms); `root` unchanged.
- **Mining:** no measurable improvement. 
 
## Use of AI
 
Claude (Anthropic) was used in v0.1, v0.2 and v0.3 for structure, code drafts and explanations. In v0.3 AI proposed the Merkle hex decoding and the mining header precompute, and assisted me with the benchmark. I accepted both changes after checking them (8 tests pass, identical `root` and `hash`, benchmark). However, I rejected the hash function change because I wanted to keep my hash unchanged, so results are not mixed with a hash change.