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
    // text.question ve text.answer kullanıyoruz
    var questionText = text.question ? text.question : "No question provided";
    var answerText = text.answer ? text.answer : "No answer provided";

    var newDiv = document.createElement("div");
    var h2_q = document.createElement("h2");
    var h2_a = document.createElement("h2");

    newDiv.className = 'flashcard';
    h2_q.setAttribute('style', "border-top: 1px solid red; padding: 15px; margin-top: 30px");

    // Soru metnini ayarla
    h2_q.innerHTML = questionText;

    // Cevap metnini temizleyip ayarla
    let cleanedAnswer = answerText.replace(/##/g, '').replace(/Example:/g, '');
    h2_a.setAttribute('style', "display: none; color: red; text-align: center; border-top: 1px solid red; padding: 15px; margin-top: 30px");
    h2_a.innerHTML = cleanedAnswer;

    newDiv.appendChild(h2_q);
    newDiv.appendChild(h2_a);

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
    .then(response => response.json())
    .then(data => {
        divMaker(data); // Display new card with example sentence
        question.value = ''; // Clear input fields
        answer.value = '';
    })
    .catch(error => console.error('Error:', error));
}

// Function to delete all flashcards
function delCard() {
    fetch('/api/flashcards/delete', {
        method: 'DELETE'
    })
    .then(() => {
        flashcards.innerHTML = '';  // Clear all flashcards from the page
    })
    .catch(error => console.error('Error:', error));
}

// Function to hide the "create flashcard" box
function hideBox() {
    boxx.style.display = 'none';
}

// Function to show the "create flashcard" box
function newShown() {
    boxx.style.display = 'block';
}
