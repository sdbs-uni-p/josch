import isJsonSchemaSubset from 'is-json-schema-subset';

const path = require("path");
const path1 = path.resolve(process.argv[2]);
const path2 = path.resolve(process.argv[3]);
const result = path.resolve(process.argv[4]);

const fs = require('fs')


/**
 * Compare two schemas for semantic relations and stores the relation at the specified path.
 *
 * @param path1 The path to the first schema
 * @param path2 The path to the second schema
 * @param result The path to the result.
 */
async function compare(path1, path2, result) {

    // Get the schemas
    let schema1, schema2, relation;

    try {
        schema1 = require(path1)
    } catch (e) {
        schema1 = fs.readFileSync(path1)
    }
    try {
        schema2 = require(path2)
    } catch (e) {
        schema2 = fs.readFileSync(path2)
    }

    // Remove drafts because this tool only accepts draft-05 but draft-04 is given.
    schema1.$schema = null
    schema2.$schema = null

    // Do the comparison.
    try {
        if (await isJsonSchemaSubset(schema1, schema2)) {
            if (await isJsonSchemaSubset(schema2, schema1)) {
                relation = 'equivalent';
            } else {
                relation = 'subset';
            }
        } else if (await isJsonSchemaSubset(schema2, schema1)) {
            relation = 'superset';
        } else {
            relation = 'incomparable';
        }
    } catch (err) {
        console.log('Error comparing schemas.', err);
    }

    // Write relation to the result.
    const jsonString = JSON.stringify({result: relation});
    fs.writeFile(result, jsonString, err => {
        if (err) {
            console.log('Error writing file.', err)
        } else {
            console.log('Success.')
        }
    });
}

compare(path1, path2, result);
