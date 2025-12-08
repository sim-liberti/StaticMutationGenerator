const hookPrefix = 'x-test-hook-';
const templatePrefix = 'x-test-tpl-';
const hookRegex = new RegExp(`^(${hookPrefix}|${templatePrefix})[a-z0-9\\-]+?-\\d+$`);

const elementLocator = (hookName, index) => {
    return `[@${hookName}]` + (index > 0 ? `[${index}]` : '');
}

LocatorBuilders.add('HookBased', function(element)  {
    let pathLocator = '';

    function getIndex(node) {
        let index = 1;
        let sibling = node.previousElementSibling;
        while (sibling) {
            if (sibling.tagName === node.tagName) {
                index++;
            }
            sibling = sibling.previousElementSibling;
        }
        return index;
    }

    function getHook(elem) {
        for (let i = 0, l = elem.attributes.length; i < l; ++i) {
            let match = elem.attributes[i].name.match(hookRegex);
            if (match) {
                return match[0];
            }
        }
        return null;
    }

    function hasSiblings(node) {
        let sibling = node.parentNode ? node.parentNode.firstElementChild : null;
        while (sibling) {
            if (sibling !== node && sibling.tagName === node.tagName) {
                return true;
            }
            sibling = sibling.nextElementSibling;
        }
        return false;
    }

    function isTemplate(elem) {
        for (let i = 0, l = elem.attributes.length; i < l; ++i) {
            if (elem.attributes[i].name.startsWith(templatePrefix)) {
                return true;
            }
        }
        return false;
    }

    function includesTemplates(elem) {
        let sibling = elem.parentNode ? elem.parentNode.firstElementChild : null;
        while (sibling) {
            if (sibling !== elem && isTemplate(sibling)) {
                return true;
            }
            sibling = sibling.nextElementSibling;
        }
        return false;
    }

    while (element !== document) {
        let pathElement = '';
        let hook = getHook(element);
        if (pathLocator === '' || hasSiblings(element) || isTemplate(element) || includesTemplates(element)) {
            if (hasSiblings(element)) {
                pathElement = elementLocator(hook, getIndex(element));
            } else {
                pathElement = elementLocator(hook);
            }
            pathLocator = pathElement + pathLocator;
        }
        element = element.parentNode;
    }
})
