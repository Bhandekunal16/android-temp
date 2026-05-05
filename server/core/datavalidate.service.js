class dataValidator {
  async checkMissing(currentArray, expectedArray, values) {
    for (let i = 0; i < values.length; i++) {
      if (
        values[i] == null ||
        (typeof values[i] === "string" && values[i].trim() === "")
      )
        return false;
    }

    return (
      currentArray.length === expectedArray.length &&
      [...currentArray].sort().toString() ===
        [...expectedArray].sort().toString()
    );
  }

  async checkStrings(obj) {
    const stringArray = Object.values(obj);
    for (let i = 0; i < stringArray.length; i++) {
      if (typeof stringArray[i] != "string") return false;
    }
    return true;
  }
}

module.exports = dataValidator;
