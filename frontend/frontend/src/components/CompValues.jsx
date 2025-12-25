import React, { useEffect, useState, useRef } from "react";

export default function compValues(placeholder, actualValue, form) { // Checks if data has been passed correctly to a page.

    if (actualValue != undefined && actualValue.trim().length != 0) {   
        return actualValue;
    };
    form.value = "";
    return placeholder;
}
