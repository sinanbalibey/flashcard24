const boxx = document.getElementsByClassName("create-box")[0];
const flashcards = document.getElementsByClassName("flashcards")[0];
const question = document.getElementById("question");
const answer = document.getElementById("answer");

// Fetch and display cards on page load
fetch('/api/flashcards')
    .then(response => response.json())
    .then(data => {
        data.forEach(divMaker);  // Populate flashcards from database on load
    });

// Function to display each flashcard with question, answer, and example sentence
function divMaker(text) {
    var questionText = text.question ? text.question : "No question provided";
    var answerText = text.answer ? text.answer : "No answer provided";
    var exampleSentence = text.exampleSentence ? text.exampleSentence : "";

    var newDiv = document.createElement("div");
    var idDiv = document.createElement("div");
    var h2_q = document.createElement("h2");
    var h2_a = document.createElement("h2");
    var exampleParagraph = document.createElement("p");

    newDiv.className = 'flashcard';
    newDiv.style.position = 'relative';
    h2_q.setAttribute('style', "border-top: 1px solid red; padding: 15px; margin-top: 30px; overflow: hidden; height: 100px;");
    h2_q.innerHTML = questionText;

    let cleanedAnswer = answerText.replace(/##/g, '').replace(/Example:/g, '').replace(/\*\*\*\*/g, '');
    let cleanedExample = exampleSentence.replace(/##/g, '').replace(/English:/g, '').replace(/Turkish:/g, '').replace(/\*\*\*\*/g, '');

    h2_a.setAttribute('style', "display: none; color: red; text-align: center; border-top: 1px solid red; padding: 15px; margin-top: 30px; overflow: hidden; height: 100px;");
    h2_a.innerHTML = cleanedAnswer;

    // Örnek cümleyi küçük fontlu bir paragraf olarak ekliyoruz
    exampleParagraph.className = "small-paragraph";
    exampleParagraph.innerHTML = cleanedExample;

    idDiv.setAttribute('style', "position: absolute; top: 5px; left: 5px; font-size: 12px; color: grey;");
    idDiv.innerText = `ID: ${text.id}`;

    newDiv.appendChild(idDiv);
    newDiv.appendChild(h2_q);
    newDiv.appendChild(h2_a);
    h2_a.appendChild(exampleParagraph); // h2_a'ya paragrafı ekliyoruz

    newDiv.addEventListener('click', function () {
        if (h2_a.style.display == "none") {
            h2_a.style.display = "block";
            h2_q.style.display = "none";
        } else {
            h2_a.style.display = "none";
            h2_q.style.display = "block";
        }
    });

    flashcards.appendChild(newDiv);
}




// Function to add a new flashcard
function addCard() {
    const FlashObject = {
        question: question.value,
        answer: answer.value
    };

    // Send new card to backend
    fetch('/api/flashcards/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(FlashObject)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(errorMessage => { throw new Error(errorMessage); });
        }
        return response.json();
    })
    .then(data => {
        divMaker(data); // Display new card with example sentence
        question.value = ''; // Clear input fields
        answer.value = '';
    })
    .catch(error => {
        alert(error.message); // Display the error message as an alert
        console.error('Error:', error);
    });
}

function delOneCard() {
    const id = document.querySelector('.buttonss input').value; // Get ID from input field
    if (!id) {
        alert("Please enter an ID.");
        return;
    }

    fetch(`/api/flashcards/delete/${id}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (response.status === 204) {
            alert(`Flashcard with ID ${id} deleted.`);
            flashcards.innerHTML = ''; // Clear all flashcards
            fetchFlashcards(); // Reload flashcards
        } else {
            alert("Flashcard not found.");
        }
    })
    .catch(error => console.error('Error:', error));
}


// Function to delete all flashcards
function delCard() {
    if (confirm("Tüm kartları silmek istediğinize emin misiniz?")) {
        fetch('/api/flashcards/delete', {
            method: 'DELETE'
        })
        .then(() => {
            flashcards.innerHTML = '';  // Clear all flashcards from the page
        })
        .catch(error => console.error('Error:', error));
    }
}

// Function to hide the "create flashcard" box
function hideBox() {
    boxx.style.display = 'none';
}

function fetchFlashcards() {
    fetch('/api/flashcards')
        .then(response => response.json())
        .then(data => {
            flashcards.innerHTML = ''; // Clear existing cards
            data.forEach(divMaker); // Re-populate flashcards from database
        });
}

fetchFlashcards();

// Function to show the "create flashcard" box
function newShown() {
    boxx.style.display = 'block';
}
