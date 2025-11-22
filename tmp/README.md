Based on the JSON responses you provided, here's an analysis of the Antminer API (CGMiner API) fields:

## JSON Field Analysis

### Common Fields in Both Responses:

**STATUS array:**
- `STATUS`: "S" (Success) or "E" (Error)
- `When`: Unix timestamp of response
- `Code`: Status code (70 = stats response)
- `Msg`: Human-readable message
- `Description`: CGMiner version

**Root level:**
- `id`: Request ID (matches your request ID)

### Normal Mode Specific Fields (STATS[1]):

**Basic Miner Info:**
- `BMMiner`: Miner software version
- `Miner`: Miner firmware/component version
- `CompileTime`: Firmware compilation timestamp
- `Type`: Miner model

**Operational Stats:**
- `Elapsed`: Uptime in seconds
- `Mode`: **Work mode** (2 = Sleep mode in your "normal" response!)
- `GHS 5s`: Current hash rate (5-second average)
- `GHS av`: Average hash rate
- `frequency`: Operating frequency

**Temperature Sensors:**
- `temp_num`: Number of temperature sensors
- `temp1`, `temp2`, `temp3`: Chip temperatures
- `temp2_1`, `temp2_2`, `temp2_3`: Board temperatures
- `temp_pcb1-4`, `temp_chip1-4`, `temp_pic1-4`: Detailed temp arrays

**Fan Information:**
- `fan_num`: Number of fans
- `fan1-4`: Individual fan RPMs

**Chain/Board Information:**
- `chain_acn1-4`: Number of working ASICs per chain
- `chain_rate1-4`: Hash rate per chain
- `chain_hw1-4`: Hardware errors per chain

### Sleep Mode Response:
The sleep mode response is minimal - it only returns basic miner identification without operational data, which makes sense since the miner isn't operating.

## Important Discovery

**Critical Note**: Your "normal mode" response actually shows `"Mode": 2` which indicates the miner is in **Sleep Mode**, not normal mode! Mode values typically are:
- `0` = Normal
- `1` = Low Power
- `2` = Sleep
- `3` = High Performance

## API Documentation Sources

1. **Official CGMiner Documentation**:
    - GitHub: https://github.com/ckolivas/cgminer
    - Look for API-README or protocol documentation

2. **Bitmain Documentation**:
    - Check Bitmain's official support site
    - Product-specific API documentation

3. **Community Resources**:
    - Braiins OS Documentation: https://docs.braiins.com/
    - Miner API repositories on GitHub
    - Mining forum discussions

4. **Reverse Engineering**:
    - Many mining software projects have reverse-engineered the API
    - Examples: Awesome Miner, Hive OS, Minerstat

## Key API Commands

Besides `stats`, common CGMiner API commands include:
- `{"command":"summary"}` - Basic summary
- `{"command":"devs"}` - Detailed device info
- `{"command":"pools"}` - Pool information
- `{"command":"version"}` - Version info
- `{"command":"mode","parameter":"0"}` - Set work mode

## Recommendations

1. **Verify the actual mode** - your "normal mode" response suggests the miner might actually be in sleep mode
2. **Test different modes** to see the complete field differences
3. **Check the miner's web interface** to correlate API responses with actual status
4. **Monitor real-time changes** to understand dynamic field behavior

The API follows CGMiner's standard but with Bitmain-specific extensions for their hardware sensors and features.