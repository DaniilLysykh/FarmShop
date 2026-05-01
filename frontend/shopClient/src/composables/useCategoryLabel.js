export function useCategoryLabel() {
  const categoryLabels = {
    MILK: 'Молочные продукты',
    MEAT: 'Мясо',
    VEGETABLES: 'Овощи',
    HONEY: 'Мёд',
    EGGS: 'Яйца',
    BREAD: 'Хлебобулочные изделия'
  };

  const getCategoryLabel = (category) => {
    return categoryLabels[category] || category;
  };

  return { getCategoryLabel, categoryLabels };
}