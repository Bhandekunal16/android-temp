const { MongoClient } = require("mongodb");

class databaseService {
  #url;
  client;

  constructor() {
    this.#url = process.env.MONGO_URL;
    if (!this.#url) {
      throw new Error("MONGO_URL is not set. Did you create server/.env?");
    }
    this.client = new MongoClient(this.#url);
  }

  connect = async () => await this.client.connect();

  getMongoDb = (dbName) => this.client.db(dbName);
}

module.exports = databaseService;