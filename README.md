# azure-math-func
The program has three functions, which can be called with https://\<Domain-Name\>/api/\<Function\>/?\<Attribute\>=\<Value\> <br>
<!-- The domain name is in my case https://javamathfunctions.azurewebsites.net -->
The functions are "primeFactors", "factorial", "pi" <br>
Attribute for primeFactors and factorial is "number" and for pi "digits" <br>
The value for all numbers is a whole positive number <br>
<br>
If the function needs longer than 20 seconds, it kills itself. <br>
<br>
## Details function "pi"
The function pi uses the <a href="https://en.wikipedia.org/wiki/Chudnovsky_algorithm">chundnovsky algorithm<a> to calculate pi, because it's the fastest algorithm for computers. <br>
I mostly copied the source code from a <a href="https://stackoverflow.com/questions/66649378/wrong-result-computing-pi-with-the-chudnovsky-algorithm">Stack Overflow thread<a> and corrected some things. <br>
<br>
## Details function "factorial" and "primeFactors"
The computing algorithms of those function were almost entirely written by Github CoPilot. But I had to make some improvements, so the algorithms are more efficient. <br>
<br>
## Ability to kill itself
For the ability to kill itself after 20 seconds, I used threads. At beginning, a new thread is created with the task to execute the wished function. <br>
Then the thread gets started by the program and the current time + 20 seconds is measured. <br>
If the current time exceeds the measured time, it will interrupt the program and returns an error code 500.
