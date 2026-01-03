// Debug form submission for property editing
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const isEditOperation = document.querySelector('input[type="hidden"][name="id"]') !== null;
    
    if (form && isEditOperation) {
        console.log('Edit operation detected, setting up form validation');
        
        // Log all form inputs for debugging
        const formInputs = form.querySelectorAll('input, select, textarea');
        console.log('Form inputs:', formInputs.length);
        
        formInputs.forEach(input => {
            console.log(`Input: ${input.name}, Type: ${input.type}, Value: ${input.value}`);
            
            // For checkboxes, add change listener to log changes
            if (input.type === 'checkbox') {
                input.addEventListener('change', function() {
                    console.log(`Checkbox ${this.name} with value ${this.value} changed to ${this.checked ? 'checked' : 'unchecked'}`);
                });
            }
        });
        
        form.addEventListener('submit', function(e) {
            console.log('Form submission started');
            console.log('Form Action:', this.action);
            
            const propertyId = document.querySelector('input[type="hidden"][name="id"]').value;
            console.log('Property ID:', propertyId);
            
            // Validate form action URL
            if (!this.action.includes('/edit/' + propertyId)) {
                console.error('Form URL does not include property ID:', propertyId);
                e.preventDefault();
                alert('Error in form submission URL. Please try again or contact support.');
                return false;
            }
            
            // Log all form values being submitted
            const formData = new FormData(this);
            console.log('Form data being submitted:');
            for (let [key, value] of formData.entries()) {
                console.log(`${key}: ${value}`);
            }
            
            // Check if any images are marked for removal
            const removedImages = formData.getAll('removeImages');
            if (removedImages.length > 0) {
                console.log('Images marked for removal:', removedImages);
            }
            
            console.log('Form submission validated, proceeding with submission');
            return true;
        });
    }
}); 