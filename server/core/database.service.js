const { MongoClient } = require("mongodb");
const config = require("../config/config.json");

class databaseService {
  #url = config.mongoUrl;
  client;

  constructor() {
    this.client = new MongoClient(this.#url);
  }

  connect = async () => await this.client.connect();

  getMongoDb = (dbName) => this.client.db(dbName);
}

module.exports = databaseService;