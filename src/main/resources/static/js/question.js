const questionField = document.querySelectorAll('.question-field');
const inputField = document.getElementById('chat-input');
questionField.forEach(function (question) {
    question.addEventListener('click', function (event) {
        console.log(event.target.text);
        inputField.value = event.target.text;
    })
});
