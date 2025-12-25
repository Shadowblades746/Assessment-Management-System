import React, { useEffect, useState, useRef } from "react";

export default function hasData(requiredData, actualData) { // Checks if data has been passed correctly to a page.
    if (!actualData || actualData.constructor !== Object) {
        return false;
    }
    return requiredData.every((field) => field in actualData);
}
